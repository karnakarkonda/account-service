package com.techie.microservices.accountservice.exception;

public class AccountAlreadyExists extends RuntimeException {
    public AccountAlreadyExists(String message) {
        super(message);
    }
}
