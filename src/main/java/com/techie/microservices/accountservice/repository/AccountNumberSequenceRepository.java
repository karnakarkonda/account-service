package com.techie.microservices.accountservice.repository;

import com.techie.microservices.accountservice.entity.AccountNumberSequence;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface AccountNumberSequenceRepository extends JpaRepository<AccountNumberSequence, Long> {

}
