// src/main/java/com/lifementor/exception/ValidationException.java
package com.lifementor.exception;

public class ValidationException extends RuntimeException {
    public ValidationException(String message) {
        super(message);
    }
}