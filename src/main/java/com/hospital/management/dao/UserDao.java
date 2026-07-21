package com.hospital.management.dao;
import com.hospital.management.model.User;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.Optional;
public interface UserDao {
    Optional<User> findByUsername(String username) throws SQLException;
    Optional<User> findByEmail(String email) throws SQLException;
    Optional<User> findById(Long userId) throws SQLException;
    boolean usernameExists(String username) throws SQLException;
    boolean emailExists(String email) throws SQLException;
    long create(User user) throws SQLException;
    long create(Connection connection, User user) throws SQLException;
    boolean updateUserIdentity(Connection connection, Long userId, String username, String email, String status) throws SQLException;
    boolean updatePassword(Connection connection, Long userId, String passwordHash) throws SQLException;
    boolean updateStatus(Connection connection, Long userId, String status) throws SQLException;
    boolean usernameExistsExcludingUser(String username, Long userId) throws SQLException;
    boolean emailExistsExcludingUser(String email, Long userId) throws SQLException;
    void updateSuccessfulLogin(Long userId) throws SQLException;
    void incrementFailedLoginAttempts(Long userId) throws SQLException;
    void lockUser(Long userId) throws SQLException;
    void resetFailedLoginAttempts(Long userId) throws SQLException;
}
