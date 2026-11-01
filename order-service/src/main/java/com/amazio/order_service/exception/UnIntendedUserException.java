package com.amazio.order_service.exception;

public class UnIntendedUserException extends RuntimeException {
    public UnIntendedUserException(String message) {
        super(message);
    }
}
