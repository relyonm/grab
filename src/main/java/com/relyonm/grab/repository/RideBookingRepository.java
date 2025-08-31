package com.relyonm.grab.repository;

import com.relyonm.grab.repository.entity.RideBookingEntity;
import org.springframework.data.repository.Repository;

import java.util.Optional;

public interface RideBookingRepository extends Repository<RideBookingEntity, Long> {

  RideBookingEntity save(RideBookingEntity rideBooking);

  Optional<RideBookingEntity> findById(Long id);
}
