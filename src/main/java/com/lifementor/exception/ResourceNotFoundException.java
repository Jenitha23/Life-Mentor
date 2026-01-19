// src/main/java/com/lifementor/exception/ResourceNotFoundException.java
package com.lifementor.exception;

public class ResourceNotFoundException extends RuntimeException {
    public ResourceNotFoundException(String message) {
        super(message);
    }
}