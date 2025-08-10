package com.relyonm.grab.controller;

import com.relyonm.grab.controller.dto.JsonPatchOperationDto;
import com.relyonm.grab.service.DriverService;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("drivers")
public class DriverRestController {

  private final DriverService driverService;

  public DriverRestController(DriverService driverService) {
    this.driverService = driverService;
  }

  @PatchMapping("{id}")
  @ResponseStatus(HttpStatus.NO_CONTENT)
  public void partialUpdate(@PathVariable Long id,
    @RequestBody List<JsonPatchOperationDto> jsonPatchOperationDtos) {
    driverService.partialUpdate(id, jsonPatchOperationDtos);
  }
}
