package com.relyonm.grab.repository.entity;

import org.springframework.data.annotation.Id;
import org.springframework.data.relational.core.mapping.Column;
import org.springframework.data.relational.core.mapping.Table;

import java.time.Instant;

@Table("passenger")
public record PassengerEntity(
  @Id Long id,
  @Column("phone_number") String phone,
  @Column("otp") String otp,
  @Column("otp_expired_at") Instant otpExpiredAt,
  @Column("fcm_token") String fcmToken
) {}
