package com.techie.microservices.accountservice.exception;

public class CustomerServiceUnavailableException extends RuntimeException {
    public CustomerServiceUnavailableException(String message,  Throwable cause ) {
        super(message, cause);
    }
}
