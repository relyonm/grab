package com.relyonm.grab.service;

import com.relyonm.grab.repository.RideBookingRepository;
import com.relyonm.grab.repository.entity.LocationEntity;
import com.relyonm.grab.repository.entity.RideBookingEntity;
import com.relyonm.grab.repository.entity.enumeration.RideBookingStatus;
import com.relyonm.grab.service.domain.RideBookingCreate;
import org.springframework.stereotype.Service;

import java.util.Set;

@Service
public class RideBookingService {

  private final RideDispatchService rideDispatchService;
  private final RideBookingRepository rideBookingRepository;

  public RideBookingService(RideDispatchService rideDispatchService, RideBookingRepository rideBookingRepository) {
    this.rideDispatchService = rideDispatchService;
    this.rideBookingRepository = rideBookingRepository;
  }

  public void create(RideBookingCreate rideBookingCreate) {
    var rideBooking = new RideBookingEntity(
      null, Long.valueOf(rideBookingCreate.passengerId()), null, new LocationEntity(rideBookingCreate
        .pickup()
        .lat(), rideBookingCreate
          .pickup()
          .lng()), new LocationEntity(rideBookingCreate.dropoff().lat(), rideBookingCreate
            .dropoff()
            .lng()), RideBookingStatus.OPEN, Set.of()
    );
    var savedRideBooking = rideBookingRepository.save(rideBooking);
    rideDispatchService.findAndNotifyNearestDriver(savedRideBooking);
  }
}
