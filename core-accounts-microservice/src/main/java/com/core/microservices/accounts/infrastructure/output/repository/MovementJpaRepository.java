package com.core.microservices.accounts.infrastructure.output.repository;

import com.core.microservices.accounts.infrastructure.output.repository.entity.MovementEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;

@Repository
public interface MovementJpaRepository extends JpaRepository<MovementEntity, Long> {

    List<MovementEntity> findByAccountId(Long accountId);
    List<MovementEntity> findByAccountIdAndDateBetween(Long accountId, LocalDateTime startDate, LocalDateTime endDate);
}