package com.techie.microservices.accountservice.repository;

import com.techie.microservices.accountservice.entity.Account;
import com.techie.microservices.accountservice.enums.AccountType;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;
import java.util.UUID;


@Repository
public interface AccountRepository extends JpaRepository<Account, Long> {
    boolean existsByCustomerIdAndAccountType(UUID customerId, AccountType accountType);

    Optional<Account> findByAccountNumber(String accountNumber);
}
