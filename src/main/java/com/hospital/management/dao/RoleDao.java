package com.hospital.management.dao;
import java.sql.SQLException;import java.util.Optional;
/** Read-only role lookup that avoids assumptions about identity values. */
public interface RoleDao { Optional<Long> findRoleIdByName(String roleName) throws SQLException; }
