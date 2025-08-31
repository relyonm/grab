package com.relyonm.grab.repository;

import com.relyonm.grab.repository.entity.DriverEntity;
import com.relyonm.grab.share.enumeration.DriverStatus;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jdbc.repository.query.Query;
import org.springframework.data.repository.Repository;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

public interface DriverRepository extends Repository<DriverEntity, Long> {

  List<DriverEntity> findAll();

  Page<DriverEntity> findAll(Pageable pageable);

  @Query("""
    SELECT * FROM driver d
    WHERE d.status = :status
      AND d.id <> ALL(:rejectedIds)
      AND d.current_lat IS NOT NULL
      AND d.current_lng IS NOT NULL
    """)
  List<DriverEntity> findAvailable(@Param("status") DriverStatus status,
    @Param("rejectedIds") Long[] rejectedIds);

  Optional<DriverEntity> findById(Long id);

  boolean existsById(Long id);

  void save(DriverEntity driver);

  void deleteById(Long id);
}
