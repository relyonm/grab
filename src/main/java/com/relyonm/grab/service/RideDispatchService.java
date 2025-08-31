package com.relyonm.grab.service;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.relyonm.grab.repository.DriverRepository;
import com.relyonm.grab.repository.PassengerRepository;
import com.relyonm.grab.repository.RideBookingRepository;
import com.relyonm.grab.repository.entity.DriverEntity;
import com.relyonm.grab.repository.entity.LocationEntity;
import com.relyonm.grab.repository.entity.PassengerEntity;
import com.relyonm.grab.repository.entity.RideBookingEntity;
import com.relyonm.grab.share.enumeration.DriverStatus;
import com.relyonm.grab.share.enumeration.RideBookingStatus;
import com.relyonm.grab.share.exception.ResourceNotFoundException;
import org.springframework.scheduling.TaskScheduler;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ScheduledFuture;
import java.util.function.Consumer;

@Service
public class RideDispatchService {

  private final NotificationService notificationService;

  private final DriverRepository driverRepository;
  private final PassengerRepository passengerRepository;
  private final RideBookingRepository rideBookingRepository;

  private final ObjectMapper objectMapper;
  private final TaskScheduler taskScheduler;
  private final Map<Long, ScheduledFuture<?>> scheduledTasks = new ConcurrentHashMap<>();

  public RideDispatchService(NotificationService notificationService, DriverRepository driverRepository, PassengerRepository passengerRepository, RideBookingRepository rideBookingRepository, ObjectMapper objectMapper, TaskScheduler taskScheduler) {
    this.notificationService = notificationService;
    this.driverRepository = driverRepository;
    this.passengerRepository = passengerRepository;
    this.rideBookingRepository = rideBookingRepository;
    this.objectMapper = objectMapper;
    this.taskScheduler = taskScheduler;
  }

  @Async
  public void findAndNotifyNearestDriver(RideBookingEntity rideBooking) {
    Optional<DriverEntity> driverOptional = findNearestAvailableDriver(
      rideBooking.pickupLocation(), rideBooking.rejectedDriverIds()
    );

    if (driverOptional.isEmpty()) {
      handleNoDriverFound(rideBooking);
      return;
    }

    var driver = driverOptional.get();
    sendRideBooking(rideBooking.pickupLocation(), rideBooking.dropoffLocation(), driver);

    scheduleReDispatchForUnresponsiveDriver(rideBooking.id(), driver.id());
  }

  private Optional<DriverEntity> findNearestAvailableDriver(LocationEntity pickupLocation,
    Set<Long> rejectedDriverIds) {
    return driverRepository
      .findAvailable(DriverStatus.AVAILABLE, Optional
        .ofNullable(rejectedDriverIds)
        .orElse(Collections.emptySet())
        .toArray(Long[]::new))
      .stream()
      .min(Comparator.comparing(driver -> calculateDriverDistance(driver, pickupLocation)));
  }

  private double calculateDriverDistance(DriverEntity driver, LocationEntity pickupLocation) {
    return Math
      .sqrt(Math.pow(pickupLocation.lat() - driver.currentLocation().lat(), 2) + Math
        .pow(pickupLocation.lat() - driver.currentLocation().lng(), 2));
  }

  private void handleNoDriverFound(RideBookingEntity rideBooking) {
    rideBookingRepository.save(new RideBookingEntity(
      rideBooking.id(), rideBooking.passengerId(), rideBooking.driverId(), rideBooking.pickupLocation(), rideBooking
        .dropoffLocation(), RideBookingStatus.NO_DRIVER_FOUND, rideBooking.rejectedDriverIds()
    ));
    sendNoDriverFoundNotification(rideBooking.passengerId());
    scheduleReDispatchForNoDriverFound(rideBooking.id());
  }

  private void sendNoDriverFoundNotification(Long passengerId) {
    PassengerEntity passenger = fetchPassenger(passengerId);
    notificationService
      .sendNotification(passenger
        .fcmToken(), "No Driver Found", "No drivers are available at the moment. We will notify you when one becomes available.");
  }

  private void sendRideBooking(LocationEntity pickupLocation, LocationEntity dropoffLocation, DriverEntity driver) {
    Map<String, Object> data = new HashMap<>();
    data.put("driverId", String.valueOf(driver.id()));
    data.put("pickup", Map.of(
      "lat", pickupLocation.lat(), "lng", pickupLocation.lng()
    ));
    data.put("dropoff", Map.of(
      "lat", dropoffLocation.lat(), "lng", dropoffLocation.lng()
    ));
    data.put("message", String.format(
      "Pickup: (%.6f, %.6f) → Dropoff: (%.6f, %.6f)", pickupLocation.lat(), pickupLocation.lng(), dropoffLocation
        .lat(), dropoffLocation.lng()
    ));

    String jsonBody;
    try {
      jsonBody = objectMapper.writeValueAsString(data);
    } catch (JsonProcessingException e) {
      throw new RuntimeException("Failed to convert notification data to JSON", e);
    }

    notificationService.sendNotification(
      driver.fcmToken(), "New Ride Booking", jsonBody
    );
  }

  // Schedule another attempt after 60 seconds
  private void scheduleReDispatchForNoDriverFound(Long rideId) {
    scheduleReDispatch(rideId, (rideBooking) -> {
    }, 60);
  }

  // Schedule re-dispatch after 20 seconds if driver does not accept/reject ride request
  private void scheduleReDispatchForUnresponsiveDriver(Long rideId, Long driverId) {
    ScheduledFuture<?> scheduledTask = scheduleReDispatch(rideId, (rideBooking) -> {
      // Driver did not respond, mark as rejected
      Set<Long> updatedRejected = new HashSet<>(rideBooking.rejectedDriverIds());
      updatedRejected.add(driverId);

      rideBookingRepository.save(new RideBookingEntity(
        rideBooking.id(), rideBooking.passengerId(), null, // driverId is null since unresponsive
        rideBooking.pickupLocation(), rideBooking.dropoffLocation(), rideBooking.status(), updatedRejected
      ));
    }, 20);

    scheduledTasks.put(rideId, scheduledTask);
  }

  private ScheduledFuture<?> scheduleReDispatch(Long rideId, Consumer<RideBookingEntity> action, long delayInSeconds) {
    return taskScheduler.schedule(() -> {
      RideBookingEntity rideBooking = rideBookingRepository.findById(rideId).orElse(null);

      if (rideBooking == null || rideBooking.status() != RideBookingStatus.NO_DRIVER_FOUND) {
        return; // The ride was already handled.
      }

      action.accept(rideBooking);

      findAndNotifyNearestDriver(rideBooking);
    }, Instant.now().plusSeconds(delayInSeconds));
  }

  private PassengerEntity fetchPassenger(Long passengerId) {
    return passengerRepository
      .findById(passengerId)
      .orElseThrow(() -> new ResourceNotFoundException("Passenger not found"));
  }
}
