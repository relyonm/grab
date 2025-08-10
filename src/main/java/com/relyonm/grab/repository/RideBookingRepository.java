package com.relyonm.grab.repository;

import com.relyonm.grab.repository.entity.RideBooking;
import org.springframework.data.repository.Repository;

public interface RideBookingRepository extends Repository<RideBooking, Long> {

  RideBooking save(RideBooking rideBookingEntity);
}
