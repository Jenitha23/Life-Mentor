// src/main/java/com/lifementor/exception/InvalidTokenException.java
package com.lifementor.exception;

public class InvalidTokenException extends RuntimeException {
    public InvalidTokenException(String message) {
        super(message);
    }
}