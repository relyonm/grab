package com.relyonm.grab.repository.entity;

import org.springframework.data.relational.core.mapping.Column;

public record LocationEntity(
  @Column("lat") Double lat,
  @Column("lng") Double lng
) {}
