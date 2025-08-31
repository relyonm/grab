package com.relyonm.grab.service.domain;

import jakarta.validation.constraints.NotBlank;

public record JsonPatchOperation(
  @NotBlank String op,
  @NotBlank String path,
  String from,     // optional, only for move/copy
  String value     // optional, only for add/replace/test
) {}
