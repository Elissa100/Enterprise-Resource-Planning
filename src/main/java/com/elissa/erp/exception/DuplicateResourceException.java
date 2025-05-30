package com.elissa.erp.exception;

import org.springframework.http.HttpStatus;

/**
 * Exception thrown when attempting to create a resource that already exists
 */
public class DuplicateResourceException extends BaseException {
    
    public DuplicateResourceException(String message) {
        super(message, HttpStatus.CONFLICT);
    }
    
    public DuplicateResourceException(String resourceName, String fieldName, Object fieldValue) {
        super(String.format("%s already exists with %s: %s", resourceName, fieldName, fieldValue), 
              HttpStatus.CONFLICT);
    }
}