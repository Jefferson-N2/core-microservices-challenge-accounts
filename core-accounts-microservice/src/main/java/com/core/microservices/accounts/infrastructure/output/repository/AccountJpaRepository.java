package com.core.microservices.accounts.infrastructure.output.repository;

import com.core.microservices.accounts.infrastructure.output.repository.entity.AccountEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface AccountJpaRepository extends JpaRepository<AccountEntity, Long> {
    
    Optional<AccountEntity> findByNumber(String number);
    List<AccountEntity> findByCustomerId(Long customerId);
}