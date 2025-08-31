package com.relyonm.grab.repository.entity;

import com.relyonm.grab.share.enumeration.RideBookingStatus;
import jakarta.annotation.Nullable;
import org.springframework.data.annotation.Id;
import org.springframework.data.relational.core.mapping.Column;
import org.springframework.data.relational.core.mapping.Embedded;
import org.springframework.data.relational.core.mapping.Table;

import java.util.Set;

@Table("ride_booking")
public record RideBookingEntity(
  @Id Long id,
  @Column("passenger_id") Long passengerId,
  @Nullable @Column("driver_id") Long driverId,
  @Embedded(onEmpty = Embedded.OnEmpty.USE_EMPTY, prefix = "pickup_") LocationEntity pickupLocation,
  @Embedded(onEmpty = Embedded.OnEmpty.USE_EMPTY, prefix = "dropoff_") LocationEntity dropoffLocation,
  @Column("status") RideBookingStatus status,
  @Column("rejected_driver_ids") Set<Long> rejectedDriverIds
) {}
