package com.bruce.erp.exception;

import org.springframework.http.HttpStatus;

import java.util.HashMap;
import java.util.Map;

/**
 * Exception thrown when there are validation errors
 */
public class ValidationException extends BaseException {
    
    private final Map<String, String> errors;
    
    public ValidationException(String message) {
        super(message, HttpStatus.BAD_REQUEST);
        this.errors = new HashMap<>();
    }
    
    public ValidationException(String message, Map<String, String> errors) {
        super(message, HttpStatus.BAD_REQUEST);
        this.errors = errors;
    }
    
    public Map<String, String> getErrors() {
        return errors;
    }
    
    public void addError(String field, String message) {
        this.errors.put(field, message);
    }
}