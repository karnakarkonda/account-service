package com.techie.microservices.accountservice.service;

import com.techie.microservices.accountservice.client.CustomerServiceClient;
import com.techie.microservices.accountservice.dto.AccountCreationRequest;
import com.techie.microservices.accountservice.dto.AccountCreationResponse;
import com.techie.microservices.accountservice.dto.AccountDetailsResponse;
import com.techie.microservices.accountservice.exception.CustomerNotFoundException;
import com.techie.microservices.accountservice.exception.CustomerServiceIntegrationException;
import com.techie.microservices.accountservice.exception.CustomerServiceUnavailableException;
import feign.FeignException;
import feign.RetryableException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.UUID;

@Service
@Slf4j
@RequiredArgsConstructor
public class AccountService {

    private final CustomerServiceClient customerServiceClient;
    private final AccountPersistenceService accountPersistenceService;

    public AccountCreationResponse create(AccountCreationRequest accountCreationRequest) {

        /**
         * Verifies that the customer exists before account creation.
         *
         */

        UUID customerId = accountCreationRequest.getCustomerId();
        verifyCustomer(customerId);
        return accountPersistenceService.createAccount(accountCreationRequest);
    }

    private void verifyCustomer(UUID customerId) {
        try {
            log.info("Verifying customer registration for customerId={}", customerId);
            customerServiceClient.getCustomerById(
                    customerId);
            log.info("Customer registration verified for customerId={}", customerId);
        } catch (RetryableException e) {
            log.error("customer service communication failed", e);
            throw new CustomerServiceUnavailableException("Customer service is currently unavailable. Please try again later.", e);
        } catch (FeignException.NotFound e) {
            log.warn("Customer registration for customerId={} was not found", customerId);
            throw new CustomerNotFoundException("customer not found with id: " + customerId);
        } catch (FeignException e) {
            log.error("customer service returned HTTP status: {}", e.status(), e);

            if (e.status() >= 500) {
                throw new CustomerServiceUnavailableException("Customer service is currently unavailable. Please try again later.", e);
            }

            throw new CustomerServiceIntegrationException(
                    "Unable to verify customer");
        }
    }

    public AccountDetailsResponse getAccount(String accountNumber) {
        return accountPersistenceService.getAccount(accountNumber);
    }

}


