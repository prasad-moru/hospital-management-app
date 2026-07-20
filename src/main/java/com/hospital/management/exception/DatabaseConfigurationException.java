package com.hospital.management.exception;

/**
 * Indicates that the application's database configuration is missing or invalid.
 */
public class DatabaseConfigurationException extends RuntimeException {

    public DatabaseConfigurationException(String message) {
        super(message);
    }

    public DatabaseConfigurationException(String message, Throwable cause) {
        super(message, cause);
    }
}
