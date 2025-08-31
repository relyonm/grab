package com.relyonm.grab.controller;

import com.relyonm.grab.controller.converter.JsonPatchConverter;
import com.relyonm.grab.controller.dto.DriverDTO;
import com.relyonm.grab.service.DriverService;
import com.relyonm.grab.service.domain.Driver;
import com.relyonm.grab.controller.dto.JsonPatchOperationDTO;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotEmpty;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

import java.io.IOException;
import java.util.List;

@RestController
@RequestMapping("drivers")
public class DriverRestController {

  private static Driver dtoToDomain(Long id, DriverDTO dto) {
    return new Driver(id, dto.currentLocation(), dto.fcmToken(), dto.status());
  }

  private static Driver dtoToDomain(DriverDTO dto) {
    return dtoToDomain(null, dto);
  }

  private final JsonPatchConverter jsonPatchConverter;
  private final DriverService driverService;

  public DriverRestController(JsonPatchConverter jsonPatchConverter, DriverService driverService) {
    this.jsonPatchConverter = jsonPatchConverter;
    this.driverService = driverService;
  }

  @GetMapping
  public List<Driver> findAll() {
    return driverService.findAll();
  }

  @PostMapping("/search")
  public Page<Driver> search(Pageable pageable) {
    return driverService.search(pageable);
  }

  @GetMapping("/{id}")
  public Driver findById(@PathVariable Long id) {
    return driverService.findById(id);
  }

  @PostMapping
  @ResponseStatus(HttpStatus.CREATED)
  public void create(@RequestBody @Valid DriverDTO driver) {
    driverService.create(dtoToDomain(driver));
  }

  @PutMapping("/{id}")
  @ResponseStatus(HttpStatus.NO_CONTENT)
  public void replace(@PathVariable Long id, @RequestBody @Valid DriverDTO driver) {
    driverService.replace(dtoToDomain(id, driver));
  }

  @PatchMapping("{id}")
  @ResponseStatus(HttpStatus.NO_CONTENT)
  public void partialUpdate(
    @PathVariable Long id,
    @RequestBody @Valid @NotEmpty List<JsonPatchOperationDTO> jsonPatchOperations) throws IOException {
    driverService.partialUpdate(id, jsonPatchConverter.toJsonNode(jsonPatchOperations));
  }

  @DeleteMapping("/{id}")
  @ResponseStatus(HttpStatus.NO_CONTENT)
  public void delete(@PathVariable Long id) {
    driverService.delete(id);
  }
}
