package com.hospital.management.util;
import org.junit.jupiter.api.Test;import static org.junit.jupiter.api.Assertions.*;
class PasswordUtilTest {@Test void hashesAndVerifies(){String h=PasswordUtil.hashPassword("temporary test input");assertNotEquals("temporary test input",h);assertTrue(PasswordUtil.verifyPassword("temporary test input",h));assertFalse(PasswordUtil.verifyPassword("wrong input",h));}@Test void rejectsBlank(){assertThrows(IllegalArgumentException.class,()->PasswordUtil.hashPassword(" "));}}
