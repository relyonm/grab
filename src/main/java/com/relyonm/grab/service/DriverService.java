package com.relyonm.grab.service;

import com.relyonm.grab.controller.dto.JsonPatchOperationDto;
import com.relyonm.grab.repository.DriverRepository;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class DriverService {

  private final DriverRepository driverRepository;

  public DriverService(DriverRepository driverRepository) {
    this.driverRepository = driverRepository;
  }

  public void partialUpdate(Long id, List<JsonPatchOperationDto> patchOperations) {
    driverRepository
      .partialUpdate(id, patchOperations
        .stream()
        .collect(Collectors
          .toMap(JsonPatchOperationDto::path, JsonPatchOperationDto::value
          )));
  }
}
