package com.relyonm.grab.controller.converter;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.relyonm.grab.controller.dto.JsonPatchOperationDTO;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public class JsonPatchConverter {

  private final ObjectMapper objectMapper;

  public JsonPatchConverter(ObjectMapper objectMapper) {
    this.objectMapper = objectMapper;
  }

  public JsonNode toJsonNode(List<JsonPatchOperationDTO> operations) {
    return objectMapper.valueToTree(operations);
  }
}
