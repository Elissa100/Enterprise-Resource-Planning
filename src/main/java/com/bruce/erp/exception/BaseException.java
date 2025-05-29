package com.bruce.erp.exception;

import lombok.Getter;
import org.springframework.http.HttpStatus;

/**
 * Base exception class for all application exceptions
 */
@Getter
public abstract class BaseException extends RuntimeException {
    
    private final HttpStatus status;
    
    public BaseException(String message, HttpStatus status) {
        super(message);
        this.status = status;
    }
    
    public BaseException(String message, Throwable cause, HttpStatus status) {
        super(message, cause);
        this.status = status;
    }
}