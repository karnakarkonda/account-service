package com.techie.microservices.accountservice.dto;


import com.techie.microservices.accountservice.enums.AccountType;
import com.techie.microservices.accountservice.enums.Currency;
import jakarta.validation.constraints.NotNull;
import lombok.*;

import java.util.UUID;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class AccountCreationRequest {

    @NotNull(message = "customerId is required")
    private UUID customerId;

    @NotNull(message = "accountType is required")
    private AccountType accountType;

    @NotNull(message = "currency is required")
    private Currency currency;
}