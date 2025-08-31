package com.relyonm.grab.controller;

import com.relyonm.grab.service.RideBookingService;
import com.relyonm.grab.service.domain.RideBookingCreate;
import jakarta.validation.Valid;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("ride-bookings")
public class RideBookingRestController {

  private final RideBookingService rideBookingService;

  public RideBookingRestController(RideBookingService rideBookingService) {
    this.rideBookingService = rideBookingService;
  }

  @PostMapping
  public void create(@RequestBody @Valid RideBookingCreate rideBookingPersistDto) {
    rideBookingService.create(rideBookingPersistDto);
  }
}
