package com.techie.microservices.accountservice.controller;


import com.techie.microservices.accountservice.dto.AccountCreationRequest;
import com.techie.microservices.accountservice.dto.AccountCreationResponse;
import com.techie.microservices.accountservice.dto.AccountDetailsResponse;
import com.techie.microservices.accountservice.service.AccountService;
import jakarta.validation.Valid;
import jakarta.validation.constraints.Pattern;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/accounts")
@RequiredArgsConstructor
@Slf4j
@Validated
public class AccountController {

    private final AccountService accountService;

    @PostMapping
    public ResponseEntity<AccountCreationResponse> create(
            @Valid @RequestBody AccountCreationRequest request) {
        log.info("Received request to Create Account for customer {}", request.getCustomerId());
        AccountCreationResponse response = accountService.create(request);
        log.info("Account created successfully for customer {}", request.getCustomerId());
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(response);
    }

    @GetMapping("/{accountNumber}")
    public ResponseEntity<AccountDetailsResponse> getAccount(
            @PathVariable
            @Pattern(
                    regexp = "^\\d{11}$",
                    message = "Account number must contain exactly 11 digits"
            )
            String accountNumber) {
        log.info("Received request to retrieve account details for {}", accountNumber);
        AccountDetailsResponse response = accountService.getAccount(accountNumber);
        log.info("Returning account details for {}", accountNumber);
        return ResponseEntity.ok(response);
    }

}
