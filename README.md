# Online Hospital Management System

An MCA final-semester academic project that will provide a centralized web application for managing hospital operations. The current phase establishes a secure, maintainable Java web application foundation; hospital business modules have not yet been implemented.

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

This project intentionally does not use Spring Boot, Hibernate, JPA, or `jakarta.servlet`.

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

The committed file `src/main/resources/db.properties.example` documents the required Oracle connection settings. Before database functionality is introduced:

1. Copy it to `src/main/resources/db.properties`.
2. Replace the placeholders with local database credentials.
3. Keep the new file private; `db.properties` is excluded by `.gitignore`.

Credentials must never be committed or hardcoded. JDBC connections will be managed through HikariCP when the data-access phase begins.

## Current implementation status

- Maven WAR project and package structure created
- Tomcat 9 / Servlet 4.0 deployment descriptor configured
- Responsive Bootstrap landing page created
- Custom 404 and 500 pages configured
- Logging and database configuration templates added
- Database SQL placeholders added
- Authentication and hospital business functionality not yet implemented

## Planned modules

- Authentication and role management
- Patient management
- Doctor management
- Department management
- Appointment management
- Medical records
- Prescriptions
- Billing and payments
- Admission and discharge
- Inventory
- Reports
- Audit logs
