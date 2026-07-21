-- Run this script while connected as HOSPITAL_APP after schema.sql and seed-data.sql.
-- Generate a BCrypt hash locally and replace REPLACE_WITH_BCRYPT_HASH before execution.
-- Never place the plain-text password in this file or commit a locally modified hash.

DECLARE
    v_role_id       ROLES.ROLE_ID%TYPE;
    v_department_id DEPARTMENTS.DEPARTMENT_ID%TYPE;
    v_user_id       USERS.USER_ID%TYPE;
    v_password_hash USERS.PASSWORD_HASH%TYPE := 'REPLACE_WITH_BCRYPT_HASH';
BEGIN
    IF v_password_hash = '$2a$12$cwA1spSR8lL846.yoKpR4esAEdA9QnP83s.3t7td/QvFFRdfVaxBu' THEN
        RAISE_APPLICATION_ERROR(-20001, 'Replace REPLACE_WITH_BCRYPT_HASH with a BCrypt hash before execution.');
    END IF;

    SELECT ROLE_ID INTO v_role_id FROM ROLES WHERE UPPER(ROLE_NAME) = 'DOCTOR';
    SELECT DEPARTMENT_ID INTO v_department_id
      FROM DEPARTMENTS
     WHERE UPPER(DEPARTMENT_NAME) = 'GENERAL MEDICINE';

    BEGIN
        SELECT USER_ID INTO v_user_id FROM USERS WHERE UPPER(USERNAME) = 'DEMO_DOCTOR';
    EXCEPTION
        WHEN NO_DATA_FOUND THEN
            INSERT INTO USERS (USERNAME, PASSWORD_HASH, EMAIL, ROLE_ID, STATUS)
            VALUES ('demo_doctor', v_password_hash, 'demo.doctor@example.com', v_role_id, 'ACTIVE')
            RETURNING USER_ID INTO v_user_id;
    END;

    INSERT INTO DOCTORS
        (USER_ID, DEPARTMENT_ID, REGISTRATION_NUMBER, FIRST_NAME, LAST_NAME,
         SPECIALIZATION, QUALIFICATION, PHONE, CONSULTATION_FEE, STATUS)
    SELECT v_user_id, v_department_id, 'REG-DEMO-001', 'Demo', 'Doctor',
           'General Medicine', 'MBBS', '9000000001', 500, 'ACTIVE'
      FROM DUAL
     WHERE NOT EXISTS (SELECT 1 FROM DOCTORS WHERE USER_ID = v_user_id)
       AND NOT EXISTS (SELECT 1 FROM DOCTORS WHERE UPPER(REGISTRATION_NUMBER) = 'REG-DEMO-001');

    COMMIT;
EXCEPTION
    WHEN OTHERS THEN
        ROLLBACK;
        RAISE;
END;
/
