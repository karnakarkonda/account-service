package com.techie.microservices.accountservice.controller;


import com.techie.microservices.accountservice.dto.AccountCreationRequest;
import com.techie.microservices.accountservice.dto.AccountCreationResponse;
import com.techie.microservices.accountservice.service.AccountService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1/accounts")
@RequiredArgsConstructor
@Slf4j
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
}
