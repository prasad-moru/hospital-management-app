-- Admission-to-billing link migration. Run once as HOSPITAL_APP after V011.
-- The nullable source key preserves existing items and prevents duplicate room charges.
ALTER TABLE BILL_ITEMS ADD ADMISSION_ID NUMBER;

ALTER TABLE BILL_ITEMS ADD CONSTRAINT FK_BILL_ITEMS_ADMISSION
    FOREIGN KEY (ADMISSION_ID) REFERENCES ADMISSIONS (ADMISSION_ID);

ALTER TABLE BILL_ITEMS ADD CONSTRAINT UK_BILL_ITEMS_ADMISSION
    UNIQUE (ADMISSION_ID);

COMMENT ON COLUMN BILL_ITEMS.ADMISSION_ID IS
    'Admission source for a server-calculated ROOM charge; null for other bill items';
