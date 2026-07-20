package com.hospital.management.exception;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertSame;

class DatabaseConfigurationExceptionTest {

    @Test
    void preservesMessage() {
        DatabaseConfigurationException exception = new DatabaseConfigurationException("invalid config");
        assertEquals("invalid config", exception.getMessage());
    }

    @Test
    void preservesMessageAndCause() {
        Throwable cause = new IllegalArgumentException("bad value");
        DatabaseConfigurationException exception =
                new DatabaseConfigurationException("invalid config", cause);
        assertEquals("invalid config", exception.getMessage());
        assertSame(cause, exception.getCause());
    }
}
