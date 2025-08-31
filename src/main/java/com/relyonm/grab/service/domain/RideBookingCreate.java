package com.relyonm.grab.service.domain;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

public record RideBookingCreate(
  @NotBlank String passengerId,
  @NotNull @Valid Location pickup,
  @NotNull @Valid Location dropoff
) {}
