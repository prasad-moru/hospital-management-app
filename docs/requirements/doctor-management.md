# Doctor Management

## Purpose and access

Doctor Management is an ADMIN-only module for listing, searching, creating, viewing, editing, activating, and deactivating doctors. `/admin/doctors*` is protected by the authentication and role-authorization filters; UI visibility is not treated as authorization.

## Data and transactions

Every doctor has one `USERS` account and one `DOCTORS` profile. Creation inserts both rows on one JDBC connection with auto-commit disabled. Update changes user identity, an optional password, and the doctor profile in the same transaction. Activation and deactivation update both statuses atomically. Any failed write or commit causes rollback, and caller-owned transaction connections are never controlled by DAOs.

Primary keys are Oracle identity values. The DOCTOR role and General Medicine department are found by name rather than fixed IDs. Passwords are accepted only for create or an intentional edit-time replacement, validated server-side, immediately BCrypt-hashed, and never logged, redisplayed, audited, or selected by Doctor queries. A blank edit password preserves the existing hash.

## Department and status rules

New assignments and reassignments require an ACTIVE department. An edit form may display its currently assigned inactive department so the existing value is not lost. Activation is blocked for an inactive assigned department and for a linked `LOCKED` user. Deactivation changes both the doctor and user to `INACTIVE`, which prevents authentication. It is blocked when scheduled/confirmed appointments exist today or later, or when an available doctor schedule exists. Doctors are never hard deleted; schedules and appointments are not changed automatically.

## Search, filters, and pagination

Case-insensitive search covers registration number, name, specialization, qualification, phone, username, email, department, and statuses. Search wildcards are escaped and all values are bound through `PreparedStatement`. Optional department and ACTIVE/INACTIVE filters share list/count conditions. Oracle `OFFSET/FETCH` pagination uses one-based pages, a default size of 10, and a maximum normalized size of 100.

## Validation and duplicates

Server validation covers account identifiers, email, BCrypt-compatible password length and complexity, required department, registration format, names, professional fields, phone, fee range/scale, and ACTIVE/INACTIVE status. Duplicate username, email, and registration checks provide field feedback; Oracle unique constraints remain the concurrency-safe final guard and `ORA-00001` is mapped to a safe duplicate result.

## Audit and security

Successful operations record `DOCTOR_CREATED`, `DOCTOR_UPDATED`, `DOCTOR_ACTIVATED`, or `DOCTOR_DEACTIVATED`. Blocked deactivation records `DOCTOR_DEACTIVATION_BLOCKED`. Events use entity `DOCTOR`, the doctor ID, current administrator ID, client IP, and user agent. Audit failure is logged without reversing an already committed business operation. CSRF tokens protect all state-changing forms, JSP output is escaped, status changes are POST-only, and SQL exceptions are never returned to the browser.

## Test coverage

JUnit 5 and Mockito tests cover service listing/lookups, validation, duplicate and department conflicts, transaction commit/rollback, optional password updates, activation/deactivation rules, resilient audit behavior, and servlet request/response forwarding and redirects. Tests do not require Oracle.

## Demo doctor setup

Generate a local BCrypt hash, replace `REPLACE_WITH_BCRYPT_HASH` in `database/create-demo-doctor.sql` without committing that local value, and run the script as `HOSPITAL_APP`. The script finds the DOCTOR role and General Medicine department, inserts missing linked records, commits atomically, and contains no plain-text password.

## Known limitations and future work

The current module manages administrative identity and professional profile data. Live Oracle/Tomcat behavior still requires local validation. Clinical features such as schedules, appointments, encounter notes, medical records, and prescriptions remain separate future modules.
