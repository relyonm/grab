package com.relyonm.grab.service.domain;

import com.relyonm.grab.share.enumeration.DriverStatus;

public record Driver(
  Long id,
  Location currentLocation,
  String fcmToken,
  DriverStatus status
) {

  public static Driver newDriver(String fcmToken) {
    return new Driver(null, null, fcmToken, DriverStatus.OFFLINE);
  }
}
