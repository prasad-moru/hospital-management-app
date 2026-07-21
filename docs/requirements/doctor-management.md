# Doctor Management - Persistence Foundation

## Phase scope

This phase supplies the persistence foundation for the future ADMIN Doctor Management module. The service layer, Servlets, JSP pages, demo-doctor workflow, and runtime UI are intentionally still pending.

## Data model and relationships

Each `DOCTORS` row has exactly one linked `USERS` row through `USER_ID` and belongs to one `DEPARTMENTS` row. Doctor reads join `DOCTORS`, `USERS`, `DEPARTMENTS`, and `ROLES`, and accept only linked users whose role name is `DOCTOR`. The `Doctor` model exposes safe account identity fields but never selects or maps `PASSWORD_HASH`.

## DAO responsibilities

`DoctorDao` provides joined lookup by doctor ID and case-insensitive registration number, paged search, duplicate registration checks, doctor-row writes, and efficient blocking checks for future appointments and active schedules. It contains persistence operations rather than business workflow decisions.

`RoleDao` looks up identity-generated role IDs by case-insensitive name, avoiding fixed role IDs. `DepartmentDao.findActiveDepartments()` returns alphabetically sorted active departments for future form selection. Existing inactive assignments remain readable through normal department and joined doctor lookup methods.

## Transaction boundary strategy

Doctor creation and updates will span both `USERS` and `DOCTORS`. Consequently, write methods on `UserDao` and `DoctorDao` accept a caller-supplied `Connection`. These methods prepare and close their own statements/result sets but do not open, close, commit, roll back, or change auto-commit on the connection. The future Doctor service will own the transaction boundary so partial user/doctor records cannot be committed.

## Search, filters, and pagination

Search is case-insensitive across registration number, names and full name, specialization, qualification, phone, username, email, department name, doctor status, and user status. `%`, `_`, and the escape character are escaped before binding. Optional department and doctor-status filters share exactly the same bound conditions in list and count queries.

Results are ordered by first name, last name, and doctor ID. Oracle pagination uses `OFFSET ? ROWS FETCH NEXT ? ROWS ONLY`, using the existing one-based `PageRequest` convention.

## Integrity and blocking checks

Registration-number lookups and duplicate checks are case-insensitive; Oracle's named unique constraint remains the final race-condition guard. `SqlExceptionUtil` recognizes Oracle error code `1` (`ORA-00001`) through chained SQL exceptions without parsing or exposing SQL text.

The appointment check uses `TRUNC(APPOINTMENT_DATE) >= TRUNC(SYSDATE)` and only `SCHEDULED` or `CONFIRMED` statuses. The schedule check uses `IS_AVAILABLE = 1`. Both use existence-style queries with `FETCH FIRST 1 ROW ONLY`.

## Remaining work

- Transactional Doctor service and safe duplicate-conflict mapping
- ADMIN list/create/view/edit/status Servlets
- CSRF-protected JSP pages
- Secure demo-doctor SQL
- Audit-event integration
- Tomcat and Oracle runtime validation
