package com.relyonm.grab.service;

import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.exc.InvalidFormatException;
import com.flipkart.zjsonpatch.JsonPatch;
import com.flipkart.zjsonpatch.JsonPatchApplicationException;
import com.relyonm.grab.service.domain.JsonPatchOperation;
import com.relyonm.grab.share.exception.PartialUpdateException;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.util.List;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

/**
 * Component responsible for applying JSON Patch operations on a given source object.
 * <p>
 * Uses {@link com.flipkart.zjsonpatch.JsonPatch} to apply the operations,
 * then maps the result back into the desired target class.
 */
@Component
public class PartialUpdateComponent {

  private static final Pattern MISSING_FIELD_PATTERN = Pattern.compile("Missing field \\\"(.*?)\\\"");
  private final ObjectMapper objectMapper;

  public PartialUpdateComponent(ObjectMapper objectMapper) {
    this.objectMapper = objectMapper;
  }

  /**
   * Applies a list of JSON Patch operations to a given source object and returns the updated object.
   *
   * @param source      the original object to update
   * @param operations  list of JSON Patch operations to apply
   * @param targetClass the expected return class
   * @param <T>         generic type of the return value
   * @return the updated object of type {@code T}
   * @throws IOException if the conversion between object and JSON fails
   */
  public <T> T partialUpdate(Object source,
    List<JsonPatchOperation> operations,
    Class<T> targetClass) throws IOException {
    try {
      // Convert patch operations and source object into JSON tree nodes
      var patchOperations = objectMapper.valueToTree(operations);
      var sourceNode = objectMapper.convertValue(source, JsonNode.class);

      // Apply JSON Patch to the source JSON
      var patchedNode = JsonPatch.apply(patchOperations, sourceNode);

      // Convert patched JSON back into the desired target class
      return objectMapper.treeToValue(patchedNode, targetClass);

    } catch (JsonPatchApplicationException e) {
      // Thrown when a patch refers to a non-existent JSON path
      throw new PartialUpdateException(buildNonExistentMessage(e), e);

    } catch (InvalidFormatException e) {
      // Thrown when a field receives an invalid type value
      throw new PartialUpdateException(buildInvalidTypeMessage(e), e);
    }
  }

  // ----------------------
  // Error message helpers
  // ----------------------

  private static String buildNonExistentMessage(JsonPatchApplicationException e) {
    var invalidField = extractInvalidField(e.getMessage());
    var invalidPath = e.getPath() + "/" + invalidField;
    return "'" + invalidPath + "' is not a valid JSON path";
  }

  private static String buildInvalidTypeMessage(InvalidFormatException e) {
    var path = e
      .getPath()
      .stream()
      .map(JsonMappingException.Reference::getFieldName)
      .collect(Collectors.joining("/"));

    var invalidValue = String.valueOf(e.getValue());
    var expectedType = mapToFriendlyType(e.getTargetType());

    return String.format("Invalid value '%s' for path '%s'. Expected a valid %s", invalidValue, path, expectedType);
  }

  // ----------------------
  // Utility helpers
  // ----------------------

  private static String extractInvalidField(String message) {
    var matcher = MISSING_FIELD_PATTERN.matcher(message);
    return matcher.find() ? matcher.group(1) : null;
  }

  private static String mapToFriendlyType(Class<?> targetType) {
    if (Number.class.isAssignableFrom(targetType) || targetType.isPrimitive()) return "number";
    if (Boolean.class.equals(targetType) || boolean.class.equals(targetType)) return "boolean";
    if (CharSequence.class.isAssignableFrom(targetType)) return "string";
    if (java.time.temporal.Temporal.class.isAssignableFrom(targetType) || java.util.Date.class
      .isAssignableFrom(targetType)) return "date/time";
    return "value";
  }
}
