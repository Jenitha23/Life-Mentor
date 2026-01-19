// src/main/java/com/lifementor/exception/AuthenticationException.java
package com.lifementor.exception;

public class AuthenticationException extends RuntimeException {
    public AuthenticationException(String message) {
        super(message);
    }
}