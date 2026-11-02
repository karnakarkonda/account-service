package com.techie.microservices.accountservice.client;

import com.techie.microservices.accountservice.dto.CustomerRegistrationResponse;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

import java.util.UUID;

@FeignClient(name = "customer-service", url = "${customer.service.url}")
public interface CustomerServiceClient {

    @GetMapping("/api/v1/customers/{customerId}/registration")
    public CustomerRegistrationResponse getCustomerById(@PathVariable UUID customerId);
}