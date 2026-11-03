package com.techie.microservices.accountservice.service;

import com.techie.microservices.accountservice.dto.AccountCreationRequest;
import com.techie.microservices.accountservice.dto.AccountCreationResponse;
import com.techie.microservices.accountservice.dto.AccountDetailsResponse;
import com.techie.microservices.accountservice.entity.Account;
import com.techie.microservices.accountservice.entity.AccountNumberSequence;
import com.techie.microservices.accountservice.enums.AccountStatus;
import com.techie.microservices.accountservice.exception.AccountAlreadyExists;
import com.techie.microservices.accountservice.exception.AccountNotFoundException;
import com.techie.microservices.accountservice.repository.AccountNumberSequenceRepository;
import com.techie.microservices.accountservice.repository.AccountRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.dao.DataIntegrityViolationException;
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

        if (accountRepository.existsByCustomerIdAndAccountType(
                accountCreationRequest.getCustomerId(), accountCreationRequest.getAccountType())) {
            log.warn("account already exists for customerId: {} and account type: {}", accountCreationRequest.getCustomerId(), accountCreationRequest.getAccountType());
            throw new AccountAlreadyExists("Account already exists for the given customer and account type");
        }

        Account account = new Account();
        account.setAccountNumber(generateAccountNumber());
        account.setCustomerId(accountCreationRequest.getCustomerId());
        account.setAccountType(accountCreationRequest.getAccountType());
        account.setStatus(AccountStatus.ACTIVE);
        account.setBalance(new BigDecimal("0.00"));
        account.setCurrency(accountCreationRequest.getCurrency());

        Account savedAccount;
        try {
            savedAccount = accountRepository.saveAndFlush(account);
        } catch (DataIntegrityViolationException e) {
            if (isDuplicateAccountConstraint(e)) {
                log.warn("Duplicate account detected for customerId: {}",
                        account.getCustomerId());
                throw new AccountAlreadyExists(
                        "Account already exists for the given customer and account type"
                );
            }
            throw e;
        }

        log.info("created account for customerId: {}", savedAccount.getAccountNumber());
        return mapToResponse(savedAccount);
    }

    private boolean isDuplicateAccountConstraint(Throwable exception) {

        Throwable cause = exception;
        while (cause != null) {
            if (cause instanceof org.hibernate.exception.ConstraintViolationException ex) {
                return "uk_customer_account_type".equalsIgnoreCase(
                        ex.getConstraintName()
                );
            }
            cause = cause.getCause();
        }
        return false;
    }

    @Transactional(readOnly = true)
    public AccountDetailsResponse getAccount(String accountNumber) {
        log.info("Retrieving account details for accountNumber: {}", accountNumber);
        Account account = accountRepository.findByAccountNumber(accountNumber)
                .orElseThrow(() -> new AccountNotFoundException("Account not found for account number: " + accountNumber));
        log.info("Retrieved account details for accountNumber: {}", accountNumber);
        return mapToAccountDetailsResponse(account);
    }


    private AccountDetailsResponse mapToAccountDetailsResponse(Account account) {
        AccountDetailsResponse response = new AccountDetailsResponse();
        response.setAccountNumber(account.getAccountNumber());
        response.setCustomerId(account.getCustomerId());
        response.setAccountType(account.getAccountType());
        response.setBalance(account.getBalance());
        response.setId(account.getId());
        response.setStatus(account.getStatus());
        response.setCurrency(account.getCurrency());
        response.setCreatedAt(account.getCreatedAt());
        return response;
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
