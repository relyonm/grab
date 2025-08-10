package com.relyonm.grab.repository;

import com.relyonm.grab.repository.entity.Driver;
import com.relyonm.grab.repository.entity.enumeration.DriverStatus;
import org.springframework.data.jdbc.repository.query.Query;
import org.springframework.data.repository.Repository;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Map;
import java.util.Set;

public interface DriverRepository extends Repository<Driver, Long>, PartialUpdateRepository<Long> {

  @Query("SELECT d FROM Driver d WHERE d.status = :status AND d.id NOT IN :rejectedIds AND d.currentLocation IS NOT NULL")
  List<Driver> findAvailableDrivers(@Param("status") DriverStatus status,
    @Param("rejectedIds") Set<Long> rejectedIds);

  void partialUpdate(Long id, Map<String, Object> changes);
}
