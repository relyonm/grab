package com.relyonm.grab.controller.dto;

import com.relyonm.grab.service.domain.Location;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

public record RideBookingDTO(
  @NotBlank String passengerId,
  @NotNull @Valid Location pickup,
  @NotNull @Valid Location dropoff
) {}
