package com.relyonm.grab.repository.entity;

import org.springframework.data.annotation.Id;
import org.springframework.data.relational.core.mapping.Column;
import org.springframework.data.relational.core.mapping.Embedded;
import org.springframework.data.relational.core.mapping.Table;

@Table("driver")
public record Driver(
  @Id Long id,
  @Embedded(onEmpty = Embedded.OnEmpty.USE_EMPTY, prefix = "current_") Location currentLocation,
  @Column("fcm_token") String fcmToken
) {}
