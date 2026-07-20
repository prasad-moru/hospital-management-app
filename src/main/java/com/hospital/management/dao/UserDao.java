package com.hospital.management.dao;
import com.hospital.management.model.User;
import java.sql.SQLException;
import java.util.Optional;
public interface UserDao {
    Optional<User> findByUsername(String username) throws SQLException;
    Optional<User> findByEmail(String email) throws SQLException;
    Optional<User> findById(Long userId) throws SQLException;
    boolean usernameExists(String username) throws SQLException;
    boolean emailExists(String email) throws SQLException;
    long create(User user) throws SQLException;
    void updateSuccessfulLogin(Long userId) throws SQLException;
    void incrementFailedLoginAttempts(Long userId) throws SQLException;
    void lockUser(Long userId) throws SQLException;
    void resetFailedLoginAttempts(Long userId) throws SQLException;
}
