package com.techie.microservices.accountservice.dto;

import com.techie.microservices.accountservice.enums.AccountStatus;
import com.techie.microservices.accountservice.enums.AccountType;
import com.techie.microservices.accountservice.enums.Currency;
import lombok.*;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.UUID;

@Setter
@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class AccountCreationResponse {

    private Long id;
    private String accountNumber;
    private UUID customerId;
    private AccountType accountType;
    private Currency currency;
    private BigDecimal balance;
    private AccountStatus status;
    private LocalDateTime createdAt;
}
