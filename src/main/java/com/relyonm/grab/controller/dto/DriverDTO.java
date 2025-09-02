package com.relyonm.grab.controller.dto;

import jakarta.validation.constraints.NotNull;

public record DriverDTO(@NotNull String fcmToken) {}
