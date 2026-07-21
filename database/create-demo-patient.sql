-- Generate a BCrypt hash locally and replace only REPLACE_WITH_BCRYPT_HASH.
-- Never store or publish the corresponding plain-text password.
-- The safety check validates BCrypt structure, so replacing the placeholder globally is safe.

DECLARE
    v_role ROLES.ROLE_ID%TYPE;
    v_user USERS.USER_ID%TYPE;
    v_hash USERS.PASSWORD_HASH%TYPE := '$2a$12$cwA1spSR8lL846.yoKpR4esAEdA9QnP83s.3t7td/QvFFRdfVaxBu';
BEGIN
    IF LENGTH(v_hash) != 60
       OR SUBSTR(v_hash, 1, 4) NOT IN ('$2a$', '$2b$', '$2y$') THEN
        RAISE_APPLICATION_ERROR(-20001, 'Replace the placeholder with a valid 60-character BCrypt hash');
    END IF;

    SELECT ROLE_ID INTO v_role FROM ROLES WHERE ROLE_NAME = 'PATIENT';

    BEGIN
        SELECT USER_ID INTO v_user FROM USERS WHERE UPPER(USERNAME) = 'DEMO_PATIENT';
    EXCEPTION
        WHEN NO_DATA_FOUND THEN
            INSERT INTO USERS (USERNAME, PASSWORD_HASH, EMAIL, ROLE_ID, STATUS)
            VALUES ('demo_patient', v_hash, 'demo.patient@example.com', v_role, 'ACTIVE')
            RETURNING USER_ID INTO v_user;
    END;

    INSERT INTO PATIENTS
        (USER_ID, PATIENT_NUMBER, FIRST_NAME, LAST_NAME, DATE_OF_BIRTH,
         GENDER, BLOOD_GROUP, PHONE, EMAIL, STATUS)
    SELECT v_user, 'PAT-DEMO-001', 'Demo', 'Patient', DATE '1990-01-01',
           'OTHER', 'O+', '9000000002', 'demo.patient@example.com', 'ACTIVE'
      FROM DUAL
     WHERE NOT EXISTS (
         SELECT 1 FROM PATIENTS
          WHERE USER_ID = v_user OR PATIENT_NUMBER = 'PAT-DEMO-001'
     );

    COMMIT;
EXCEPTION
    WHEN OTHERS THEN
        ROLLBACK;
        RAISE;
END;
/
