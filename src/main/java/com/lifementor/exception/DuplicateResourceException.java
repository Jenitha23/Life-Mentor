// src/main/java/com/lifementor/exception/DuplicateResourceException.java
package com.lifementor.exception;

public class DuplicateResourceException extends RuntimeException {
    public DuplicateResourceException(String message) {
        super(message);
    }
}