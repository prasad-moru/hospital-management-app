package com.hospital.management.util;

import org.mindrot.jbcrypt.BCrypt;

/** BCrypt password hashing and verification helpers. */
public final class PasswordUtil {
    private static final int WORK_FACTOR = 12;
    private PasswordUtil() { throw new IllegalStateException("Utility class"); }
    public static String hashPassword(String plainPassword) {
        validate(plainPassword);
        return BCrypt.hashpw(plainPassword, BCrypt.gensalt(WORK_FACTOR));
    }
    public static boolean verifyPassword(String plainPassword, String passwordHash) {
        validate(plainPassword);
        if (passwordHash == null || passwordHash.isBlank()) return false;
        try { return BCrypt.checkpw(plainPassword, passwordHash); }
        catch (IllegalArgumentException ignored) { return false; }
    }
    private static void validate(String value) {
        if (value == null || value.isBlank()) throw new IllegalArgumentException("Password must not be blank");
    }
}
