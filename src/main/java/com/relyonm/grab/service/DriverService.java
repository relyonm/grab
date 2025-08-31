package com.relyonm.grab.service;

import com.relyonm.grab.repository.DriverRepository;
import com.relyonm.grab.repository.entity.DriverEntity;
import com.relyonm.grab.service.domain.JsonPatchOperation;
import com.relyonm.grab.share.exception.ResourceNotFoundException;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.util.List;

@Service
public class DriverService {

  private final PartialUpdateComponent partialUpdateComponent;
  private final DriverRepository driverRepository;

  public DriverService(PartialUpdateComponent partialUpdateComponent, DriverRepository driverRepository) {
    this.partialUpdateComponent = partialUpdateComponent;
    this.driverRepository = driverRepository;
  }

  public void partialUpdate(Long id,
    List<JsonPatchOperation> jsonPatchOperations) throws IOException {
    var driver = driverRepository
      .findById(id)
      .orElseThrow(() -> new ResourceNotFoundException("Driver with id " + id + " not found."));
    var updatedDriver = partialUpdateComponent.partialUpdate(driver, jsonPatchOperations, DriverEntity.class);
    driverRepository.save(updatedDriver);
  }
}
