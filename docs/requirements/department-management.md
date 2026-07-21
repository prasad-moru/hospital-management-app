# Department Management

The ADMIN-only Department Management module maintains department name, description, location, status, and database timestamps. Admins can list, search, page, create, view, edit, activate, and deactivate records at `/admin/departments`. Departments are never hard deleted.

Names are trimmed, required, 2-100 characters, and case-insensitively unique. Description is optional up to 500 characters, location is optional up to 150, and status is ACTIVE or INACTIVE. Server validation remains authoritative. Duplicate checks occur on create and update; the Oracle unique constraint provides final concurrency protection.

Search covers name, description, location, and status with escaped wildcard input. Oracle pagination uses `OFFSET` and `FETCH NEXT`, ordered by name. Deactivation is rejected while ACTIVE doctors reference the department. Activation verifies that the department still exists.

All routes are protected by authentication and ADMIN authorization filters. State changes are POST-only with session CSRF tokens. JSP output uses `c:out`, identifiers are validated, SQL uses prepared statements, browser responses receive safe errors, and created timestamps cannot be mass-assigned. Successful changes emit `DEPARTMENT_CREATED`, `DEPARTMENT_UPDATED`, `DEPARTMENT_ACTIVATED`, and `DEPARTMENT_DEACTIVATED` audit events with user, IP, and user-agent context.

Unit coverage includes all validation boundaries, listing, create/update duplicates and missing records, activation, deactivation conflict, safe SQL failure handling, and servlet routing. Known limitations include no bulk operations, import/export, department history screen, or optimistic locking. Future work can add those features plus database-backed integration tests.
