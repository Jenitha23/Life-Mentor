// src/main/java/com/lifementor/exception/AccountLockedException.java
package com.lifementor.exception;

public class AccountLockedException extends RuntimeException {
    public AccountLockedException(String message) {
        super(message);
    }
}