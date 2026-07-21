# Patient Management

Patient Management lets ADMIN and RECEPTIONIST users search, page, register, view, edit, activate, and deactivate patients at `/admin/patients`. A patient can exist without a login (`PATIENTS.USER_ID` is null), or can have one PATIENT-role `USERS` account.

Patient numbers use `PAT-YYYY-NNNNNN` backed by `PATIENT_NUMBER_SEQ`; supplied numbers are normalized uppercase. Creating a linked account inserts `USERS` and `PATIENTS` on one JDBC transaction. Updates can add an account to an unlinked patient or update linked identity and an optional replacement password. Accounts cannot be unlinked and roles are not changed. Passwords are validated, BCrypt-hashed, never selected by patient queries, redisplayed, logged, or audited.

Validation covers names, ISO birth dates from 1900 through today, gender, blood group, phone, optional email/address/emergency contact, status, and conditional account credentials. Prepared statements, database unique constraints, duplicate pre-checks, CSRF, escaped JSP output, POST-only status changes, and server-side role authorization protect the module.

Activation updates patient and linked user atomically but never unlocks `LOCKED` users. Deactivation is blocked by scheduled/confirmed appointments today or later and `ADMITTED` admissions. It never deletes history or cancels appointments. Audit events are `PATIENT_CREATED`, `PATIENT_UPDATED`, `PATIENT_LOGIN_ACCOUNT_CREATED`, `PATIENT_ACTIVATED`, `PATIENT_DEACTIVATED`, and `PATIENT_DEACTIVATION_BLOCKED`; audit failure does not reverse committed work.

Run the V005 migration for an existing schema, or use the updated full schema for a reset. For the demo patient, replace the explicit BCrypt placeholder in `create-demo-patient.sql` locally and run it as `HOSPITAL_APP`. Current limitations: admission management, appointments, clinical history, and self-service patient screens remain future modules. Unit tests use Mockito and require no Oracle instance.
