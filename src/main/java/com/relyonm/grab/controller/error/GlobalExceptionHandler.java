package com.relyonm.grab.controller.error;

import com.fasterxml.jackson.core.JsonParseException;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.exc.MismatchedInputException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ProblemDetail;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.method.annotation.HandlerMethodValidationException;

import java.util.Arrays;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

@RestControllerAdvice
public class GlobalExceptionHandler {

  private static final String MSG_BODY_MISSING = "Request body is missing";
  private static final String MSG_INVALID_STRUCTURE = "Invalid JSON structure: expected %s, actual %s";
  private static final String MSG_MALFORMED_JSON = "Malformed JSON request";
  private static final String MSG_INVALID_BODY = "Invalid request body";

  @ResponseStatus(HttpStatus.BAD_REQUEST)
  @ExceptionHandler(HttpMessageNotReadableException.class)
  public ProblemDetail handleHttpMessageNotReadable(HttpMessageNotReadableException ex) {
    return switch (ex.getCause()) {
      case null -> badRequest(MSG_BODY_MISSING);
      case MismatchedInputException mie -> handleMismatchedInput(mie);
      case JsonParseException ignored -> badRequest(MSG_MALFORMED_JSON);
      case JsonMappingException jme when jme.getCause() instanceof JsonParseException -> badRequest(MSG_MALFORMED_JSON);
      default -> badRequest(MSG_INVALID_BODY);
    };
  }

  @ExceptionHandler(HandlerMethodValidationException.class)
  public ResponseEntity<ProblemDetail> handleHandlerMethodValidationException(
    HandlerMethodValidationException ex) {

    var detail = ProblemDetail.forStatus(HttpStatus.BAD_REQUEST);
    detail.setTitle("Validation failed");

    Map<String, String> errors = new HashMap<>();
    ex.getAllErrors().forEach(error -> {
      String field = ((FieldError) error).getField();
      String message = error.getDefaultMessage();
      errors.put(field, message);
    });

    detail.setProperty("errors", errors);
    return ResponseEntity.badRequest().body(detail);
  }

  private static ProblemDetail badRequest(String message) {
    ProblemDetail pd = ProblemDetail.forStatus(HttpStatus.BAD_REQUEST);
    pd.setDetail(message);
    return pd;
  }

  private static ProblemDetail handleMismatchedInput(MismatchedInputException mie) {
    String expected = detectExpected(mie.getTargetType());
    String actual = JacksonTokenToJsonType.fromMessage(mie.getOriginalMessage());
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
