package com.relyonm.grab.controller.dto;

import jakarta.validation.constraints.NotNull;

public record LocationDTO(
  @NotNull Double lat,
  @NotNull Double lng
) {}
