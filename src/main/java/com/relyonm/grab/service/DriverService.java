package com.relyonm.grab.service;

import com.relyonm.grab.repository.DriverRepository;
import com.relyonm.grab.repository.entity.DriverEntity;
import com.relyonm.grab.repository.entity.LocationEntity;
import com.relyonm.grab.service.domain.Driver;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
public class DriverService {

  private static DriverEntity domainToEntity(Driver driver) {
    return new DriverEntity(driver.id(), Optional
      .ofNullable(driver.currentLocation())
      .map(location -> new LocationEntity(location.lat(), location.lng()))
      .orElse(null), driver.fcmToken(), driver.status());
  }

  private final DriverRepository driverRepository;

  public DriverService(DriverRepository driverRepository) {
    this.driverRepository = driverRepository;
  }

  public void create(Driver driver) {
    driverRepository.save(domainToEntity(driver));
  }
}
