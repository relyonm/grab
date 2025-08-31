package com.relyonm.grab.service;

import com.relyonm.grab.repository.RideBookingRepository;
import com.relyonm.grab.repository.entity.LocationEntity;
import com.relyonm.grab.repository.entity.RideBookingEntity;
import com.relyonm.grab.share.enumeration.RideBookingStatus;
import com.relyonm.grab.service.domain.RideBooking;
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

  public void create(RideBooking rideBooking) {
    var entity = new RideBookingEntity(
      rideBooking.id(), Long.valueOf(rideBooking.passengerId()), null, new LocationEntity(rideBooking
        .pickup()
        .lat(), rideBooking
          .pickup()
          .lng()), new LocationEntity(rideBooking.dropoff().lat(), rideBooking
            .dropoff()
            .lng()), RideBookingStatus.OPEN, Set.of()
    );
    var savedRideBooking = rideBookingRepository.save(entity);
    rideDispatchService.findAndNotifyNearestDriver(savedRideBooking);
  }
}
