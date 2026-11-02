package com.techie.microservices.accountservice.exception;

public class CustomerServiceIntegrationException extends RuntimeException {

    public CustomerServiceIntegrationException(String message) {
        super(message);
    }

}
