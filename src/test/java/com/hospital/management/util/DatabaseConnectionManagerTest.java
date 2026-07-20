package com.hospital.management.util;

import com.hospital.management.exception.DatabaseConfigurationException;
import org.junit.jupiter.api.Test;

import java.util.Properties;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

class DatabaseConnectionManagerTest {

    @Test
    void validationAcceptsCompleteConfiguration() {
        Properties properties = completeProperties();
        assertDoesNotThrow(() -> DatabaseConnectionManager.validateProperties(properties));
    }

    @Test
    void validationRejectsMissingMandatoryProperty() {
        Properties properties = completeProperties();
        properties.remove("db.url");

        DatabaseConfigurationException exception = assertThrows(
                DatabaseConfigurationException.class,
                () -> DatabaseConnectionManager.validateProperties(properties)
        );
        assertEquals("Missing mandatory database property: db.url", exception.getMessage());
    }

    @Test
    void validationRejectsBlankMandatoryProperty() {
        Properties properties = completeProperties();
        properties.setProperty("db.password", "  ");

        assertThrows(DatabaseConfigurationException.class,
                () -> DatabaseConnectionManager.validateProperties(properties));
    }

    private Properties completeProperties() {
        Properties properties = new Properties();
        properties.setProperty("db.url", "jdbc:oracle:thin:@//localhost:1521/XEPDB1");
        properties.setProperty("db.username", "HOSPITAL_APP");
        properties.setProperty("db.password", "test-placeholder");
        properties.setProperty("db.driver", "oracle.jdbc.OracleDriver");
        properties.setProperty("db.pool.maximumPoolSize", "10");
        properties.setProperty("db.pool.minimumIdle", "2");
        properties.setProperty("db.pool.connectionTimeout", "30000");
        properties.setProperty("db.pool.idleTimeout", "600000");
        properties.setProperty("db.pool.maxLifetime", "1800000");
        return properties;
    }
}
