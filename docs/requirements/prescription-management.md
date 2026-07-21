# Prescription Management

Prescriptions are created from an existing medical record. ADMIN manages all prescriptions, DOCTOR manages only prescriptions for their linked doctor profile, and PATIENT has read-only access to their own prescriptions. Other roles are denied server-side.

`UK_RX_MED_REC` enforces one prescription per medical record. Patient, doctor, appointment, and medical-record identities are derived from the medical record and never accepted from the form. A prescription requires 1–20 medicine items. Each item includes medicine, dosage, frequency, duration, optional route, and optional instructions.

Creation inserts the header and every item in one JDBC transaction. Editing is allowed only while ACTIVE and replaces all detail rows atomically: update header, delete old items, insert replacements, then commit. Any failure rolls back the complete operation. Prescription headers are never hard-deleted.

Valid transitions are `ACTIVE → COMPLETED` and `ACTIVE → CANCELLED`; both destination states are terminal. The printable view is read-only and ownership protected.

Validation covers ISO validity dates through one year, 2000-character notes, required medicine directions, maximum lengths, supported status, and maximum 20 rows. Existing authentication, role filtering, CSRF, prepared statements, `c:out`, and CSP controls apply.

Audit events are `PRESCRIPTION_CREATED`, `PRESCRIPTION_UPDATED`, `PRESCRIPTION_COMPLETED`, `PRESCRIPTION_CANCELLED`, `PRESCRIPTION_ACCESS_DENIED`, `PRESCRIPTION_CREATION_BLOCKED`, and `PRESCRIPTION_STATUS_CHANGE_BLOCKED`. Audit payloads contain metadata only—not medicine, dosage, instructions, notes, or diagnosis.

JUnit/Mockito tests cover validation and terminal transition rules without Oracle. Billing integration, dispensing, refills, electronic signatures, and pharmacy inventory remain future work.
