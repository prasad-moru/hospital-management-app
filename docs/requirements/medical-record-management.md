# Medical Record Management

## Purpose, roles, and ownership

Medical Record Management stores clinical observations for an appointment. ADMIN can view, create, and edit all records. DOCTOR can view/create/edit only records for appointments assigned to their linked doctor profile. PATIENT has read-only access to records linked to their patient profile. RECEPTIONIST, NURSE, and other roles have no access in this phase. All ownership is enforced by the service, not merely the UI.

## Appointment dependency and lifecycle

Application-created records require an existing appointment. Patient and doctor identifiers are copied from that appointment and never accepted from the form. Only `CONFIRMED` or `COMPLETED` appointments are eligible; scheduled, cancelled, and no-show appointments are blocked. `UK_MED_REC_APPT` and a pre-insert existence check enforce one record per appointment, including concurrent requests. Records are never hard-deleted.

Creation and update use one JDBC connection with auto-commit disabled, commit on success, and rollback on failure. Editing changes clinical fields and vital signs only; appointment, patient, doctor, visit date, and created timestamp remain unchanged.

## Validation and vital signs

Symptoms and diagnosis require 3–2000 characters. Treatment notes allow 4000 and allergies allow 1000 characters. Blood pressure uses `systolic/diastolic`, with systolic 50–250, diastolic 30–150, and systolic greater than diastolic. Temperature is 30–45 °C, weight is greater than 0 and at most 500 kg, and height is greater than 0 and at most 300 cm. Numeric values allow at most two decimals.

## Security, audit, and errors

State changes use POST and the existing CSRF filter. JSP output uses `c:out`; DAO SQL uses prepared statements and never selects account passwords. Audit events are `MEDICAL_RECORD_CREATED`, `MEDICAL_RECORD_UPDATED`, `MEDICAL_RECORD_ACCESS_DENIED`, and `MEDICAL_RECORD_CREATION_BLOCKED`. Only action, record identifier, user, IP, and user agent are recorded—never symptoms, diagnosis, notes, allergies, or vital signs. SQL errors become safe service responses.

## Tests and limitations

JUnit/Mockito tests cover field validation, appointment eligibility, duplicate blocking, patient read ownership, and invalid servlet inputs without Oracle. Prescriptions are intentionally not implemented and will later reference the medical record/appointment. Existing legacy records may have a null appointment, but new application records cannot.
