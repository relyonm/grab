package com.relyonm.grab.repository.entity;

import org.springframework.data.relational.core.mapping.Column;

public record Location(
  @Column("lat") Double lat,
  @Column("lng") Double lng
) {}
