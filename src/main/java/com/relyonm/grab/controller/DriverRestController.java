package com.relyonm.grab.controller;

import com.relyonm.grab.controller.dto.DriverDTO;
import com.relyonm.grab.service.DriverService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("drivers")
public class DriverRestController {

  private final DriverService driverService;

  public DriverRestController(DriverService driverService) {
    this.driverService = driverService;
  }

  @PostMapping
  @ResponseStatus(HttpStatus.CREATED)
  public void create(@RequestBody @Valid DriverDTO driverDTO) {
//    driverService.create(Driver.newDriver(driverDTO.fcmToken()));
  }
}
