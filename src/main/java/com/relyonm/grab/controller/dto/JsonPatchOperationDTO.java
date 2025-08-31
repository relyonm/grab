package com.relyonm.grab.controller.dto;

import jakarta.validation.constraints.NotBlank;

public record JsonPatchOperationDTO(
  @NotBlank String op,
  @NotBlank String path,
  String from,     // optional, only for move/copy
  String value     // optional, only for add/replace/test
) {}
