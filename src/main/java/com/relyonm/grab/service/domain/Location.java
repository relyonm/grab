package com.relyonm.grab.service.domain;

import jakarta.validation.constraints.NotNull;

public record Location(
  @NotNull Double lat,
  @NotNull Double lng
) {}
