package com.techie.microservices.accountservice.exception;

import jakarta.validation.ConstraintViolationException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import tools.jackson.databind.exc.InvalidFormatException;

import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;
import java.util.stream.Collectors;

@Slf4j
@RestControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<ErrorResponse> handleMethodArgumentNotValidException(
            MethodArgumentNotValidException ex) {

        Map<String, String> errors = new HashMap<>();

        ex.getBindingResult()
                .getFieldErrors()
                .forEach(error ->
                        errors.put(error.getField(), error.getDefaultMessage()));

        log.warn("Validation failed: {}", errors);

        ErrorResponse response = buildErrorResponse(
                HttpStatus.BAD_REQUEST,
                "Validation failed"
        );
        response.setErrors(errors);

        return ResponseEntity.badRequest().body(response);
    }

    @ExceptionHandler(ConstraintViolationException.class)
    public ResponseEntity<ErrorResponse> handleConstraintViolationException(
            ConstraintViolationException ex) {

        log.warn("Constraint violation: {}", ex.getMessage());

        return ResponseEntity.badRequest()
                .body(buildErrorResponse(
                        HttpStatus.BAD_REQUEST,
                        ex.getMessage()));
    }

    @ExceptionHandler(HttpMessageNotReadableException.class)
    public ResponseEntity<ErrorResponse> handleHttpMessageNotReadableException(
            HttpMessageNotReadableException ex) {

        String message = "Invalid request payload.";

        Throwable cause = ex.getCause();

        if (cause instanceof InvalidFormatException invalidFormatException) {

            Class<?> targetType = invalidFormatException.getTargetType();

            if (targetType.isEnum()) {

                String fieldName = invalidFormatException.getPath().isEmpty()
                        ? "field"
                        : invalidFormatException.getPath()
                        .get(invalidFormatException.getPath().size() - 1)
                        .getPropertyName();

                String allowedValues = Arrays.stream(targetType.getEnumConstants())
                        .map(Object::toString)
                        .collect(Collectors.joining(", "));

                message = String.format(
                        "%s should be one of: %s",
                        fieldName,
                        allowedValues
                );
            }
        }

        log.warn("Invalid request payload: {}", message);

        return ResponseEntity.badRequest()
                .body(buildErrorResponse(
                        HttpStatus.BAD_REQUEST,
                        message));
    }

    @ExceptionHandler(CustomerServiceUnavailableException.class)
    public ResponseEntity<ErrorResponse> handleCustomerServiceUnavailableException(
            CustomerServiceUnavailableException ex) {

        log.error("Customer service unavailable: {}", ex.getMessage(), ex);

        return ResponseEntity.status(HttpStatus.SERVICE_UNAVAILABLE)
                .body(buildErrorResponse(
                        HttpStatus.SERVICE_UNAVAILABLE,
                        "Customer service is currently unavailable. Please try again later."));
    }

    @ExceptionHandler(CustomerNotFoundException.class)
    public ResponseEntity<ErrorResponse> handleCustomerNotFoundException(
            CustomerNotFoundException ex) {

        log.warn("Customer not found: {}", ex.getMessage());

        return ResponseEntity.status(HttpStatus.NOT_FOUND)
                .body(buildErrorResponse(
                        HttpStatus.NOT_FOUND,
                        ex.getMessage()));
    }

    @ExceptionHandler(AccountAlreadyExists.class)
    public ResponseEntity<ErrorResponse> handleAccountAlreadyExistsException(
            AccountAlreadyExists ex) {

        log.warn("Account already exists: {}", ex.getMessage());

        return ResponseEntity.status(HttpStatus.CONFLICT)
                .body(buildErrorResponse(
                        HttpStatus.CONFLICT,
                        ex.getMessage()));
    }

    @ExceptionHandler(AccountNotFoundException.class)
    public ResponseEntity<ErrorResponse> handleAccountNotFoundException(
            AccountNotFoundException ex) {

        log.warn("Account not found: {}", ex.getMessage());

        return ResponseEntity.status(HttpStatus.NOT_FOUND)
                .body(buildErrorResponse(
                        HttpStatus.NOT_FOUND,
                        ex.getMessage()));
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<ErrorResponse> handleUnhandledException(
            Exception ex) {

        log.error("Unexpected error occurred", ex);

        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(buildErrorResponse(
                        HttpStatus.INTERNAL_SERVER_ERROR,
                        "An unexpected error occurred. Please try again later."));
    }

    private ErrorResponse buildErrorResponse(
            HttpStatus status,
            String message) {

        ErrorResponse response = new ErrorResponse();
        response.setStatus(status.value());
        response.setMessage(message);
        response.setTimestamp(LocalDateTime.now());

        return response;
    }
}