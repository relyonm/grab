package com.relyonm.grab.service;

import com.relyonm.grab.controller.dto.RideBookingPersistDto;
import com.relyonm.grab.repository.RideBookingRepository;
import com.relyonm.grab.repository.entity.Location;
import com.relyonm.grab.repository.entity.RideBooking;
import com.relyonm.grab.repository.entity.enumeration.RideBookingStatus;
import org.springframework.stereotype.Service;

import java.util.Set;

@Service
public class RideBookingService {

  private final RideBookingRepository rideBookingRepository;

  public RideBookingService(RideBookingRepository rideBookingRepository) {
    this.rideBookingRepository = rideBookingRepository;
  }

  public void create(RideBookingPersistDto dto) {
    rideBookingRepository.save(new RideBooking(
      null, Long.valueOf(dto.passengerId()), null, new Location(dto.pickup().lat(), dto
        .pickup()
        .lng()), new Location(dto.dropoff().lat(), dto.dropoff().lng()), RideBookingStatus.OPEN, Set.of()
    ));
  }
}
