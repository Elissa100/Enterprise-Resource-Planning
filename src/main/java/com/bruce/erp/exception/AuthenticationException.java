package com.bruce.erp.exception;

import org.springframework.http.HttpStatus;

/**
 * Exception thrown when there is an authentication error
 */
public class AuthenticationException extends BaseException {
    
    public AuthenticationException(String message) {
        super(message, HttpStatus.UNAUTHORIZED);
    }
    
    public AuthenticationException(String message, Throwable cause) {
        super(message, cause, HttpStatus.UNAUTHORIZED);
    }
}