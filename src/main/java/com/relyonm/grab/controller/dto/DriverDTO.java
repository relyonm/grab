package com.relyonm.grab.controller.dto;

import com.relyonm.grab.service.domain.Location;
import com.relyonm.grab.share.enumeration.DriverStatus;

public record DriverDTO(Location currentLocation,
  String fcmToken,
  DriverStatus status) {}
