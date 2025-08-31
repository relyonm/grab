package com.relyonm.grab.controller.error;

import com.fasterxml.jackson.core.JsonParseException;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.exc.MismatchedInputException;
import com.relyonm.grab.share.exception.PartialUpdateException;
import jakarta.validation.ConstraintViolation;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ProblemDetail;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.method.annotation.HandlerMethodValidationException;

import java.util.Arrays;
import java.util.Collection;
import java.util.Map;
import java.util.stream.Collectors;

@RestControllerAdvice
public class GlobalExceptionHandler {

  private static final Logger LOGGER = LoggerFactory.getLogger(GlobalExceptionHandler.class);

  private static final String MSG_BODY_MISSING = "Request body is missing";
  private static final String MSG_INVALID_STRUCTURE = "Invalid JSON structure: expected %s, actual %s";
  private static final String MSG_MALFORMED_JSON = "Malformed JSON request";
  private static final String MSG_INVALID_BODY = "Invalid request body";

  @ExceptionHandler(HttpMessageNotReadableException.class)
  public ProblemDetail handleHttpMessageNotReadable(HttpMessageNotReadableException e) {
    return switch (e.getCause()) {
      case null -> badRequest(MSG_BODY_MISSING);
      case MismatchedInputException mie -> handleMismatchedInput(mie);
      case JsonParseException ignored -> badRequest(MSG_MALFORMED_JSON);
      case JsonMappingException jme when jme.getCause() instanceof JsonParseException -> badRequest(MSG_MALFORMED_JSON);
      default -> badRequest(MSG_INVALID_BODY);
    };
  }

  @ExceptionHandler(HandlerMethodValidationException.class)
  public ProblemDetail handleHandlerMethodValidationException(
    HandlerMethodValidationException e) {

    Map<String, String> errors = e.getValueResults().isEmpty() ? extractObjectErrors(e) : extractValueErrors(e);

    var detail = ProblemDetail.forStatus(HttpStatus.BAD_REQUEST);
    detail.setTitle("Validation failed");
    detail.setProperty("errors", errors);

    return detail;
  }

  @ExceptionHandler(PartialUpdateException.class)
  public ProblemDetail handlePartialUpdateException(PartialUpdateException e) {
    return ProblemDetail.forStatusAndDetail(HttpStatus.BAD_REQUEST, e.getMessage());
  }

  @ExceptionHandler(Exception.class)
  public ProblemDetail handleException(Exception e) {
    LOGGER.error(e.getMessage(), e);
    return ProblemDetail.forStatus(HttpStatus.INTERNAL_SERVER_ERROR);
  }

  private Map<String, String> extractObjectErrors(HandlerMethodValidationException e) {
    return e
      .getAllErrors()
      .stream()
      .collect(Collectors.toMap(
        error -> {
          ConstraintViolation<?> violation = ((FieldError) error).unwrap(ConstraintViolation.class);
          return trimRootPath(violation.getPropertyPath().toString());
        }, error -> error.getDefaultMessage()
      ));
  }

  private Map<String, String> extractValueErrors(HandlerMethodValidationException e) {
    return e
      .getValueResults()
      .stream()
      .collect(Collectors.toMap(
        error -> error.getMethodParameter().getParameterName(), error -> error
          .getResolvableErrors()
          .getFirst()
          .getDefaultMessage()
      ));
  }

  private static String trimRootPath(String path) {
    int dotIndex = path.indexOf('.');
    return dotIndex != -1 ? path.substring(dotIndex + 1) : path;
  }

  private static ProblemDetail badRequest(String message) {
    var problemDetail = ProblemDetail.forStatus(HttpStatus.BAD_REQUEST);
    problemDetail.setDetail(message);
    return problemDetail;
  }

  private static ProblemDetail handleMismatchedInput(MismatchedInputException mie) {
    var expected = detectExpected(mie.getTargetType());
    var actual = JacksonTokenToJsonType.fromMessage(mie.getOriginalMessage());
    return badRequest(MSG_INVALID_STRUCTURE.formatted(expected, actual));
  }

  private static String detectExpected(Class<?> targetType) {
    if (targetType == null) return "unknown";
    return switch (targetType) {
      case Class<?> t when Collection.class.isAssignableFrom(t) -> "[]";
      case Class<?> t when Map.class.isAssignableFrom(t) -> "{}";
      case Class<?> t when t.isPrimitive() || Number.class.isAssignableFrom(t) || Boolean.class
        .equals(t) || Character.class.equals(t) || String.class.equals(t) -> t.getSimpleName().toLowerCase();
      default -> "{}"; // fallback for DTOs
    };
  }

  private enum JacksonTokenToJsonType {

    OBJECT("START_OBJECT", "{}"), ARRAY("START_ARRAY", "[]"), STRING("VALUE_STRING", "string"), NUMBER("VALUE_NUMBER", "number"), BOOLEAN_TRUE("VALUE_TRUE", "boolean"), BOOLEAN_FALSE("VALUE_FALSE", "boolean");

    private final String token;
    private final String type;

    JacksonTokenToJsonType(String token, String type) {
      this.token = token;
      this.type = type;
    }

    static String fromMessage(String message) {
      return Arrays
        .stream(values())
        .filter(t -> message.contains(t.token))
        .findFirst()
        .map(t -> t.type)
        .orElseThrow(() -> new IllegalArgumentException(
          "Unrecognized Jackson token in message: " + message + ". Supported tokens: " + Arrays.toString(values())
        ));
    }
  }
}
