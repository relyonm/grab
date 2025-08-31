package com.relyonm.grab.service.domain;

public record RideBooking(
  Long id,
  String passengerId,
  Location pickup,
  Location dropoff
) {}
