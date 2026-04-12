package com.example.portal.exception;

public class ProvisionerUnavailableException extends RuntimeException {

    public ProvisionerUnavailableException(String message) {
        super(message);
    }

    public ProvisionerUnavailableException(String message, Throwable cause) {
        super(message, cause);
    }
}
