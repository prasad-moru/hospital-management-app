package com.hospital.management.util;
import java.sql.SQLException;
/** Safe Oracle exception classification helpers. */
public final class SqlExceptionUtil {private SqlExceptionUtil(){throw new IllegalStateException("Utility class");}public static boolean isUniqueConstraintViolation(SQLException exception){for(SQLException current=exception;current!=null;current=current.getNextException())if(current.getErrorCode()==1)return true;return false;}}
