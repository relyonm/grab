package com.relyonm.grab.service;

import com.fasterxml.jackson.databind.JsonNode;
import com.relyonm.grab.repository.DriverRepository;
import com.relyonm.grab.repository.entity.DriverEntity;
import com.relyonm.grab.repository.entity.LocationEntity;
import com.relyonm.grab.service.domain.Driver;
import com.relyonm.grab.service.domain.Location;
import com.relyonm.grab.share.exception.ResourceNotFoundException;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.util.List;

@Service
public class DriverService {

  private static DriverEntity domainToEntity(Driver driver) {
    return new DriverEntity(driver.id(), new LocationEntity(driver.currentLocation().lat(), driver
      .currentLocation()
      .lng()), driver.fcmToken(), driver.status());
  }

  private static Driver entityToDomain(DriverEntity entity) {
    return new Driver(entity.id(), new Location(entity.currentLocation().lat(), entity.currentLocation().lng()), entity
      .fcmToken(), entity.status());
  }

  private final PartialUpdateComponent partialUpdateComponent;
  private final DriverRepository driverRepository;

  public DriverService(PartialUpdateComponent partialUpdateComponent, DriverRepository driverRepository) {
    this.partialUpdateComponent = partialUpdateComponent;
    this.driverRepository = driverRepository;
  }

  public List<Driver> findAll() {
    return driverRepository.findAll().stream().map(DriverService::entityToDomain).toList();
  }

  public Page<Driver> search(Pageable pageable) {
    return driverRepository.findAll(pageable).map(DriverService::entityToDomain);
  }

  public Driver findById(Long id) {
    return entityToDomain(driverRepository
      .findById(id)
      .orElseThrow(() -> new ResourceNotFoundException("Driver with id " + id + " not found.")));
  }

  public void create(Driver driver) {
    driverRepository.save(domainToEntity(driver));
  }

  public void replace(Driver driver) {
    if (!driverRepository.existsById(driver.id())) throw new ResourceNotFoundException("Driver with id " + driver
      .id() + " not found.");
    driverRepository.save(domainToEntity(driver));
  }

  public void partialUpdate(Long id,
    JsonNode jsonPatchOperations) throws IOException {
    var driver = driverRepository
      .findById(id)
      .orElseThrow(() -> new ResourceNotFoundException("Driver with id " + id + " not found."));
    var updatedDriver = partialUpdateComponent.partialUpdate(driver, jsonPatchOperations, DriverEntity.class);
    driverRepository.save(updatedDriver);
  }

  public void delete(Long id) {
    driverRepository.deleteById(id);
  }
}
