package com.hospital.management.util;
/** Local helper. Run with one command-line password argument; it is not a JUnit test. */
public final class PasswordHashGenerator {private PasswordHashGenerator(){}public static void main(String[] args){if(args.length!=1){System.err.println("Usage: PasswordHashGenerator <password>");System.exit(1);}System.out.println(PasswordUtil.hashPassword(args[0]));}}
