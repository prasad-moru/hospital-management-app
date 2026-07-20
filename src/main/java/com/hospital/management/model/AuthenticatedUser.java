package com.hospital.management.model;

import java.io.Serial;
import java.io.Serializable;

/** Minimal, non-sensitive authenticated identity stored in an HTTP session. */
public final class AuthenticatedUser implements Serializable {
    @Serial private static final long serialVersionUID = 1L;
    private final Long userId;
    private final String username;
    private final String email;
    private final String roleName;

    public AuthenticatedUser(Long userId, String username, String email, String roleName) {
        this.userId = userId; this.username = username; this.email = email; this.roleName = roleName;
    }
    public Long getUserId() { return userId; }
    public String getUsername() { return username; }
    public String getEmail() { return email; }
    public String getRoleName() { return roleName; }
}
