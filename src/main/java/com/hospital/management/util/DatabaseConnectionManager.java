package com.hospital.management.util;

import com.hospital.management.exception.DatabaseConfigurationException;
import com.zaxxer.hikari.HikariConfig;
import com.zaxxer.hikari.HikariDataSource;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.io.InputStream;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.List;
import java.util.Properties;
import java.util.concurrent.atomic.AtomicReference;

/**
 * Owns the single application-wide HikariCP connection pool.
 *
 * <p>The pool is initialized lazily from {@code db.properties} on the classpath.
 * Callers receive normal JDBC connections and must close them after use so that
 * HikariCP can return them to the pool.</p>
 */
public final class DatabaseConnectionManager {

    private static final Logger LOGGER = LoggerFactory.getLogger(DatabaseConnectionManager.class);
    private static final String CONFIG_FILE = "db.properties";
    private static final List<String> REQUIRED_PROPERTIES = List.of(
            "db.url", "db.username", "db.password", "db.driver",
            "db.pool.maximumPoolSize", "db.pool.minimumIdle",
            "db.pool.connectionTimeout", "db.pool.idleTimeout", "db.pool.maxLifetime"
    );
    private static final AtomicReference<HikariDataSource> DATA_SOURCE = new AtomicReference<>();

    private DatabaseConnectionManager() {
        throw new IllegalStateException("Utility class cannot be instantiated");
    }

    /**
     * Borrows a connection from the shared application pool.
     *
     * @return a pooled JDBC connection
     * @throws SQLException when Oracle cannot provide a connection
     * @throws DatabaseConfigurationException when configuration is missing or invalid
     */
    public static Connection getConnection() throws SQLException {
        return getOrCreateDataSource().getConnection();
    }

    /**
     * Closes the shared connection pool if it has been initialized.
     * This method is safe to call repeatedly during application shutdown.
     */
    public static void shutdown() {
        HikariDataSource dataSource = DATA_SOURCE.getAndSet(null);
        if (dataSource != null) {
            LOGGER.info("Shutting down the hospital database connection pool");
            dataSource.close();
        }
    }

    static void validateProperties(Properties properties) {
        for (String key : REQUIRED_PROPERTIES) {
            String value = properties.getProperty(key);
            if (value == null || value.isBlank()) {
                throw new DatabaseConfigurationException("Missing mandatory database property: " + key);
            }
        }
    }

    private static HikariDataSource getOrCreateDataSource() {
        HikariDataSource existing = DATA_SOURCE.get();
        if (existing != null && !existing.isClosed()) {
            return existing;
        }

        synchronized (DatabaseConnectionManager.class) {
            existing = DATA_SOURCE.get();
            if (existing == null || existing.isClosed()) {
                existing = createDataSource(loadProperties());
                DATA_SOURCE.set(existing);
                LOGGER.info("Hospital database connection pool initialized");
            }
            return existing;
        }
    }

    private static Properties loadProperties() {
        Properties properties = new Properties();
        ClassLoader classLoader = DatabaseConnectionManager.class.getClassLoader();
        try (InputStream input = classLoader.getResourceAsStream(CONFIG_FILE)) {
            if (input == null) {
                throw new DatabaseConfigurationException(
                        "Database configuration file '" + CONFIG_FILE + "' was not found on the classpath. "
                                + "Copy db.properties.example to db.properties and configure local credentials."
                );
            }
            properties.load(input);
        } catch (IOException exception) {
            throw new DatabaseConfigurationException("Unable to read database configuration", exception);
        }
        validateProperties(properties);
        return properties;
    }

    private static HikariDataSource createDataSource(Properties properties) {
        try {
            HikariConfig config = new HikariConfig();
            config.setPoolName("HospitalConnectionPool");
            config.setJdbcUrl(properties.getProperty("db.url"));
            config.setUsername(properties.getProperty("db.username"));
            config.setPassword(properties.getProperty("db.password"));
            config.setDriverClassName(properties.getProperty("db.driver"));
            config.setMaximumPoolSize(parseInteger(properties, "db.pool.maximumPoolSize"));
            config.setMinimumIdle(parseInteger(properties, "db.pool.minimumIdle"));
            config.setConnectionTimeout(parseLong(properties, "db.pool.connectionTimeout"));
            config.setIdleTimeout(parseLong(properties, "db.pool.idleTimeout"));
            config.setMaxLifetime(parseLong(properties, "db.pool.maxLifetime"));
            return new HikariDataSource(config);
        } catch (IllegalArgumentException exception) {
            throw new DatabaseConfigurationException("Invalid database pool configuration", exception);
        }
    }

    private static int parseInteger(Properties properties, String key) {
        try {
            return Integer.parseInt(properties.getProperty(key));
        } catch (NumberFormatException exception) {
            throw new DatabaseConfigurationException("Database property must be an integer: " + key, exception);
        }
    }

    private static long parseLong(Properties properties, String key) {
        try {
            return Long.parseLong(properties.getProperty(key));
        } catch (NumberFormatException exception) {
            throw new DatabaseConfigurationException("Database property must be a whole number: " + key, exception);
        }
    }
}
