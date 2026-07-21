package com.hospital.management.model;
/** Non-sensitive request context attached to audit events. */
public record AuditContext(Long userId,String ipAddress,String userAgent){}
