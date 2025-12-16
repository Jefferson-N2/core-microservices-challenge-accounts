package com.core.microservices.customers.infrastructure.output.repository;

import com.core.microservices.customers.infrastructure.output.repository.entity.PersonEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface PersonJpaRepository extends JpaRepository<PersonEntity, Long> {
    
    Optional<PersonEntity> findByIdentification(String identification);
    boolean existsByIdentification(String identification);
}