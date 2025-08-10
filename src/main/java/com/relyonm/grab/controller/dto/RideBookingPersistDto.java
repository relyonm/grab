package com.relyonm.grab.controller.dto;

import jakarta.validation.constraints.NotNull;

public record RideBookingPersistDto(
  @NotNull String passengerId,
  @NotNull LocationDto pickup,
  @NotNull LocationDto dropoff
) {}
