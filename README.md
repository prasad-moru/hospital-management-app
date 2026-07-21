# Online Hospital Management System

An MCA final-semester academic project providing a secure, maintainable web application for hospital administration. Authentication, Department Management, and ADMIN Doctor Management are implemented.

## Technology stack

- Java 17
- JSP and Java Servlets 4.0 (`javax.servlet`)
- JDBC with HikariCP connection pooling
- Oracle Database
- Maven with WAR packaging
- Apache Tomcat 9
- HTML, CSS, Bootstrap 5, and JavaScript
- SLF4J and Logback
- JUnit 5 and Mockito

This project intentionally avoids application frameworks and uses the Tomcat 9 Java EE servlet namespace.

## Prerequisites

- JDK 17
- Apache Maven 3.8 or newer
- Apache Tomcat 9
- Oracle Database (Oracle XE with the `XEPDB1` service is suitable for local development)

Verify the tools are available:

```bash
java -version
mvn -version
```

## Folder structure

```text
online-hospital-management-system/
|-- database/                 # Schema, seed, and cleanup scripts
|-- docs/
|   |-- requirements/
|   |-- diagrams/
|   |-- screenshots/
|   `-- test-cases/
|-- src/
|   |-- main/
|   |   |-- java/com/hospital/management/
|   |   |   |-- controller/
|   |   |   |-- dao/
|   |   |   |-- model/
|   |   |   |-- service/
|   |   |   |-- filter/
|   |   |   |-- util/
|   |   |   `-- exception/
|   |   |-- resources/        # Logging and database configuration template
|   |   `-- webapp/           # JSP views and static web assets
|   `-- test/java/com/hospital/management/
|-- pom.xml
`-- README.md
```

Empty source and documentation directories are retained with `.gitkeep` files so the planned structure is available in version control.

## Build with Maven

From the project root, run:

```bash
mvn clean test
mvn clean package
```

The package command creates `target/online-hospital-management-system.war`.

## Deploy to Tomcat 9

1. Stop Tomcat if it is running.
2. Copy `target/online-hospital-management-system.war` into Tomcat's `webapps` directory.
3. Start Tomcat.
4. Open `http://localhost:8080/online-hospital-management-system/`.

Tomcat 9 is required because this application targets Servlet 4.0 and the `javax.servlet` API.

## Database configuration

The application connects to Oracle XE 21c through the dedicated, least-privilege `HOSPITAL_APP` schema and one application-wide HikariCP connection pool. It must never connect as Oracle `SYSTEM`. The committed `src/main/resources/db.properties.example` is a safe template; the local `db.properties` file is ignored by Git.

### Oracle and application setup

Perform these steps in order:

1. Start the Oracle container.
2. Connect to XEPDB1 as an administrative user.
3. Update and run `database/create-app-user.sql`.
4. Reconnect as HOSPITAL_APP.
5. Run `database/cleanup.sql` when resetting an existing schema.
6. Run `database/schema.sql`.
7. Run `database/seed-data.sql`.
8. Copy `db.properties.example` to `db.properties`.
9. Replace only the local password in `db.properties`.
10. Never commit `db.properties`.
11. Run `mvn clean test`.
12. Run `mvn clean package`.
13. Deploy the WAR to Tomcat 9.
14. Test `/health/database`.

Example SQL*Plus commands (replace placeholders locally; do not store a real password in shell history or source files):

```text
sqlplus ADMIN_USER@//localhost:1521/XEPDB1
@database/create-app-user.sql

sqlplus HOSPITAL_APP@//localhost:1521/XEPDB1
@database/cleanup.sql
@database/schema.sql
@database/seed-data.sql
```

Create the local runtime configuration from the project root:

```powershell
Copy-Item src/main/resources/db.properties.example src/main/resources/db.properties
```

Edit only `db.password` in that copied file. Verify it remains ignored before building:

```bash
git check-ignore src/main/resources/db.properties
mvn clean test
mvn clean package
```

After deploying `target/online-hospital-management-system.war` to Tomcat 9, check:

```text
http://localhost:8080/online-hospital-management-system/health/database
```

The endpoint returns only `UP` or `DOWN` JSON and never exposes connection details. See [Database Design](docs/requirements/database-design.md) for schema decisions, relationships, constraints, indexes, and security policy.

## Current implementation status

- Maven WAR project and package structure created
- Tomcat 9 / Servlet 4.0 deployment descriptor configured
- Responsive Bootstrap landing page created
- Custom 404 and 500 pages configured
- Logging and database configuration templates added
- Initial Oracle schema, reference seed data, and safe cleanup script added
- Reusable HikariCP connection manager and database health endpoint added
- BCrypt login/logout, session renewal, five-attempt account locking, CSRF protection, security headers, audit events, and server-side role authorization added
- Admin landing page and Department Management CRUD implemented
- ADMIN Doctor Management implemented with list, search, filters, pagination, add, view, edit, activation, and deactivation
- Patient Management implemented for ADMIN and RECEPTIONIST with optional linked login accounts
- Doctor account/profile writes use one linked `USERS` and `DOCTORS` JDBC transaction with BCrypt password hashing
- Remaining clinical and hospital operations modules are pending

## Authentication setup

Generate BCrypt hashes locally (the helper is not run by JUnit), replace only the placeholders in `database/create-demo-users.sql`, and execute that script as `HOSPITAL_APP` after schema and seed data. Never commit plaintext or known demo credentials.

```powershell
mvn -q -Dexec.mainClass=com.hospital.management.util.PasswordHashGenerator -Dexec.classpathScope=test test-compile exec:java -Dexec.args="YOUR_LOCAL_INPUT"
```

Login: `http://localhost:8080/online-hospital-management-system/login`

Dashboard: `http://localhost:8080/online-hospital-management-system/dashboard`

Admin dashboard: `http://localhost:8080/online-hospital-management-system/admin/`

Department Management: `http://localhost:8080/online-hospital-management-system/admin/departments`

Doctor Management: `http://localhost:8080/online-hospital-management-system/admin/doctors`

Patient Management: `http://localhost:8080/online-hospital-management-system/admin/patients`

For an existing database created before Patient Management, run `database/migrations/V005__patient_management_adjustments.sql` once as `HOSPITAL_APP`. A fresh reset using `schema.sql` already contains those objects; do not run the migration afterward.

ADMIN users can search, page, add, view, edit, activate, and deactivate departments. Departments are never deleted. Deactivation is blocked while active doctors are assigned.

ADMIN users can search and filter doctors by department/status, page results, and add, view, edit, activate, or deactivate doctor records. Creation and updates keep linked `USERS` and `DOCTORS` records atomic. Passwords are BCrypt-hashed; blank edit passwords preserve the current hash. Deactivation is blocked by scheduled/confirmed future appointments or active schedules.

ADMIN and RECEPTIONIST users can manage patients. Login accounts are optional; linked account creation and patient writes are atomic and BCrypt-secured. Blank patient numbers are generated from `PATIENT_NUMBER_SEQ` as `PAT-YYYY-NNNNNN`. Deactivation is blocked by future appointments or an active admission. For demo data, locally replace the BCrypt placeholder in `database/create-demo-patient.sql` and run it as `HOSPITAL_APP` without committing the hash.

For an optional demo doctor, generate a BCrypt hash locally, replace `REPLACE_WITH_BCRYPT_HASH` in `database/create-demo-doctor.sql`, and run it as `HOSPITAL_APP`. Do not commit the replacement hash and do not document the corresponding password.

Role paths are enforced by server-side filters: `/admin/*`, `/doctor/*`, `/nurse/*`, `/reception/*`, `/patient/*`, and `/billing/*`. ADMIN is accepted across all role areas. Security includes BCrypt, generic login errors, session renewal, 30-minute expiry, CSRF tokens, anti-caching/CSP headers, account locking, and audit events. Plain-text password storage is prohibited.

## Planned modules

- Authentication and role management
- Patient management
- Doctor clinical workspace, schedules, and appointment workflow
- Department management
- Appointment management
- Medical records
- Prescriptions
- Billing and payments
- Admission and discharge
- Inventory
- Reports
- Audit logs
