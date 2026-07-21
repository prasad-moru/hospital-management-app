-- Medical Record Management supporting indexes for an existing Oracle schema.
-- Run once as HOSPITAL_APP. The existing UK_MED_REC_APPT constraint already
-- guarantees at most one non-null medical record per appointment.
CREATE INDEX IDX_MED_REC_DOCTOR ON MEDICAL_RECORDS (DOCTOR_ID);
CREATE INDEX IDX_MED_REC_APPOINTMENT ON MEDICAL_RECORDS (APPOINTMENT_ID);
CREATE INDEX IDX_MED_REC_VISIT_DATE ON MEDICAL_RECORDS (VISIT_DATE);
