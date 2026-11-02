package com.techie.microservices.accountservice.service;

import com.techie.microservices.accountservice.dto.AccountCreationRequest;
import com.techie.microservices.accountservice.dto.AccountCreationResponse;
import com.techie.microservices.accountservice.entity.Account;
import com.techie.microservices.accountservice.entity.AccountNumberSequence;
import com.techie.microservices.accountservice.enums.AccountStatus;
import com.techie.microservices.accountservice.repository.AccountNumberSequenceRepository;
import com.techie.microservices.accountservice.repository.AccountRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;

@Service
@Slf4j
@RequiredArgsConstructor
public class AccountPersistenceService {

    private final AccountRepository accountRepository;
    private final AccountNumberSequenceRepository accountNumberSequenceRepository;

    @Transactional
    public AccountCreationResponse createAccount(AccountCreationRequest accountCreationRequest) {
        log.info("creating account for customerId: {}", accountCreationRequest.getCustomerId());
        Account account = new Account();
        account.setAccountNumber(generateAccountNumber());
        account.setCustomerId(accountCreationRequest.getCustomerId());
        account.setAccountType(accountCreationRequest.getAccountType());
        account.setStatus(AccountStatus.ACTIVE);
        account.setBalance(new BigDecimal("0.00"));
        account.setCurrency(accountCreationRequest.getCurrency());

        Account savedAccount = accountRepository.save(account);
        log.info("created account for customerId: {}", savedAccount.getAccountNumber());
        return mapToResponse(savedAccount);
    }

    private AccountCreationResponse mapToResponse(Account savedAccount) {
        AccountCreationResponse accountCreationResponse = new AccountCreationResponse();

        accountCreationResponse.setAccountNumber(savedAccount.getAccountNumber());
        accountCreationResponse.setCustomerId(savedAccount.getCustomerId());
        accountCreationResponse.setAccountType(savedAccount.getAccountType());
        accountCreationResponse.setBalance(savedAccount.getBalance());
        accountCreationResponse.setId(savedAccount.getId());
        accountCreationResponse.setStatus(savedAccount.getStatus());
        accountCreationResponse.setCurrency(savedAccount.getCurrency());
        accountCreationResponse.setCreatedAt(savedAccount.getCreatedAt());

        return accountCreationResponse;
    }

    private String generateAccountNumber() {
        AccountNumberSequence sequence = accountNumberSequenceRepository
                .save(new AccountNumberSequence());
        Long id = sequence.getId();
        return String.format("%011d", id);
    }
}
