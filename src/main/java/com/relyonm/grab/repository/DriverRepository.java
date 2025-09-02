package com.relyonm.grab.repository;

import com.relyonm.grab.repository.entity.DriverEntity;
import org.springframework.data.repository.Repository;

public interface DriverRepository extends Repository<DriverEntity, Long> {

  void save(DriverEntity driver);
}
