# Doctor Schedule Management

ADMIN users manage schedules for any active doctor; DOCTOR users can list, create, view, edit, activate, and deactivate only schedules linked to their authenticated `USERS.USER_ID`. `/schedules*` is protected for ADMIN and DOCTOR and ownership is rechecked in service operations.

Times remain Oracle `VARCHAR2(5)` in validated zero-padded `HH:mm` format and map to `LocalTime`. Days are MONDAY through SUNDAY. Slot duration is 5-240 minutes, must fit the window, and must divide it exactly. `TimeSlotGenerator` returns only slot starts whose full duration fits before the end.

Overlap is checked in SQL for the same doctor and day using `newStart < existingEnd AND newEnd > existingStart`. All stored windows count, including unavailable schedules, preventing ambiguous future activation. Update and activation exclude the current schedule. There is no hard delete; status changes do not cancel or modify existing appointments.

Search covers doctor, registration, department, specialization, and day. Filters cover doctor, department, day, and availability with Oracle pagination and weekday ordering. Prepared statements, CSRF, POST-only status changes, escaped JSP output, authentication, role filtering, and service ownership protect the module.

Audit events are `DOCTOR_SCHEDULE_CREATED`, `DOCTOR_SCHEDULE_UPDATED`, `DOCTOR_SCHEDULE_ACTIVATED`, `DOCTOR_SCHEDULE_DEACTIVATED`, `DOCTOR_SCHEDULE_OVERLAP_BLOCKED`, and `DOCTOR_SCHEDULE_ACCESS_DENIED`. Audit failure does not fail completed work. Appointment booking and checking appointments before schedule changes remain future work.
