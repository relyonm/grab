package com.relyonm.grab.controller.dto;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

public record RideBookingPersistDto(
  @NotBlank String passengerId,
  @NotNull @Valid LocationDto pickup,
  @NotNull @Valid LocationDto dropoff
) {}
