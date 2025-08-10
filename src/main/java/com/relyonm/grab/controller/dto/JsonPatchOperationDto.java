package com.relyonm.grab.controller.dto;

public record JsonPatchOperationDto(String op,
  String path,
  Integer value) {}
