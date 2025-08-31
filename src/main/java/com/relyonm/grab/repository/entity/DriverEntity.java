package com.relyonm.grab.repository.entity;

import com.relyonm.grab.repository.entity.enumeration.DriverStatus;
import org.springframework.data.annotation.Id;
import org.springframework.data.relational.core.mapping.Column;
import org.springframework.data.relational.core.mapping.Embedded;
import org.springframework.data.relational.core.mapping.Table;

@Table("driver")
public record DriverEntity(
  @Id Long id,
  @Embedded(onEmpty = Embedded.OnEmpty.USE_EMPTY, prefix = "current_") LocationEntity currentLocation,
  @Column("fcm_token") String fcmToken,
  @Column("status") DriverStatus status
) {}
