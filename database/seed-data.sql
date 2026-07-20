-- Reference data for Online Hospital Management System.
-- MERGE statements avoid duplicate rows without assuming identity values.

MERGE INTO ROLES target
USING (
    SELECT 'ADMIN' ROLE_NAME, 'System administrator' DESCRIPTION FROM DUAL UNION ALL
    SELECT 'DOCTOR', 'Medical practitioner' FROM DUAL UNION ALL
    SELECT 'NURSE', 'Nursing staff' FROM DUAL UNION ALL
    SELECT 'RECEPTIONIST', 'Reception and appointment staff' FROM DUAL UNION ALL
    SELECT 'PATIENT', 'Patient portal user' FROM DUAL UNION ALL
    SELECT 'BILLING_STAFF', 'Billing and payment staff' FROM DUAL
) source
ON (target.ROLE_NAME = source.ROLE_NAME)
WHEN NOT MATCHED THEN
    INSERT (ROLE_NAME, DESCRIPTION) VALUES (source.ROLE_NAME, source.DESCRIPTION);

MERGE INTO DEPARTMENTS target
USING (
    SELECT 'General Medicine' DEPARTMENT_NAME, 'General diagnosis and non-surgical care' DESCRIPTION FROM DUAL UNION ALL
    SELECT 'Cardiology', 'Heart and cardiovascular care' FROM DUAL UNION ALL
    SELECT 'Orthopedics', 'Musculoskeletal care' FROM DUAL UNION ALL
    SELECT 'Pediatrics', 'Medical care for infants, children, and adolescents' FROM DUAL
) source
ON (target.DEPARTMENT_NAME = source.DEPARTMENT_NAME)
WHEN NOT MATCHED THEN
    INSERT (DEPARTMENT_NAME, DESCRIPTION, STATUS)
    VALUES (source.DEPARTMENT_NAME, source.DESCRIPTION, 'ACTIVE');

COMMIT;
