package com.hospital.management.dao;
public interface AuditLogDao { void recordEvent(String action, Long userId, String entityName, Long entityId, String ipAddress, String userAgent); }
