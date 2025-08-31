package com.relyonm.grab.repository;

import com.relyonm.grab.repository.entity.PassengerEntity;
import org.springframework.data.repository.Repository;

import java.util.Optional;

public interface PassengerRepository extends Repository<PassengerEntity, Long> {

  Optional<PassengerEntity> findById(Long id);
}
