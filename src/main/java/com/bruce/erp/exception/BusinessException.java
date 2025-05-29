package com.bruce.erp.exception;

import org.springframework.http.HttpStatus;

/**
 * Exception thrown when there is a business logic error
 */
public class BusinessException extends BaseException {
    
    public BusinessException(String message) {
        super(message, HttpStatus.BAD_REQUEST);
    }
    
    public BusinessException(String message, HttpStatus status) {
        super(message, status);
    }
    
    public BusinessException(String message, Throwable cause, HttpStatus status) {
        super(message, cause, status);
    }
}