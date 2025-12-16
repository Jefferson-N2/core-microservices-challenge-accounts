package com.core.microservices.customers.infrastructure.output.repository;

import com.core.microservices.customers.infrastructure.output.repository.entity.CustomerEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface CustomerJpaRepository extends JpaRepository<CustomerEntity, Long> {
    
    @Query("SELECT c FROM CustomerEntity c JOIN c.person p WHERE p.identification = :identification")
    Optional<CustomerEntity> findByIdentification(@Param("identification") String identification);
    
    @Query("SELECT CASE WHEN COUNT(c) > 0 THEN true ELSE false END FROM CustomerEntity c JOIN c.person p WHERE p.identification = :identification")
    boolean existsByIdentification(@Param("identification") String identification);
}