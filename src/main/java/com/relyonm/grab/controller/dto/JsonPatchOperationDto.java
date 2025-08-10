package com.relyonm.grab.controller.dto;

import jakarta.validation.constraints.NotBlank;

public record JsonPatchOperationDto(@NotBlank String op,
  @NotBlank String path,
  @NotBlank String value) {}
