# Appointment Management

## Purpose and roles

Appointment Management connects patients to active doctors through recurring doctor schedules. ADMIN has full access; RECEPTIONIST can create, view, edit, reschedule, confirm, and cancel; DOCTOR can view and change permitted statuses only for assigned appointments; PATIENT can view, create, reschedule, and cancel only their own appointments. All ownership checks are server-side.

## Booking and availability

The selected doctor/date resolves the weekday and loads available `DOCTOR_SCHEDULES` windows. `TimeSlotGenerator` divides each window by its configured duration. Active `SCHEDULED` and `CONFIRMED` starts and elapsed same-day slots are removed, duplicates are collapsed, and the result is sorted. The submitted end time is never trusted: the service derives it from the selected generated slot.

Creation verifies active patient, active doctor/profile account, availability, doctor conflicts, and the SQL patient-overlap rule `newStart < existingEnd AND newEnd > existingStart`. The insert is committed in one JDBC transaction and rolled back on failure. Oracle's conditional unique index provides the final race-condition guard while permitting cancelled/completed/no-show slots to be rebooked.

## Updates, rescheduling, and status

Detail edits change only reason and notes. Rescheduling retains patient, doctor, and status and revalidates availability with the current appointment excluded. Both operate only on `SCHEDULED` or `CONFIRMED` records. No hard delete exists.

Transitions are `SCHEDULED -> CONFIRMED/CANCELLED` and `CONFIRMED -> COMPLETED/CANCELLED/NO_SHOW`. `COMPLETED`, `CANCELLED`, and `NO_SHOW` are terminal. Invalid transitions return conflict.

## Search, security, audit, and errors

The DAO uses bound Oracle SQL for case-insensitive search, role-scoped filters, date/status filters, and `OFFSET/FETCH` pagination. CSRF protects every state-changing POST; the authorization filter restricts routes and the service restricts records. Safe service messages hide SQL details.

Events include `APPOINTMENT_CREATED`, `APPOINTMENT_UPDATED`, `APPOINTMENT_RESCHEDULED`, status events, `APPOINTMENT_BOOKING_BLOCKED`, `APPOINTMENT_ACCESS_DENIED`, and `APPOINTMENT_STATUS_CHANGE_BLOCKED`. They contain identity/request metadata but not notes or medical details. Audit failure does not undo a successful operation.

## Tests and limitations

JUnit/Mockito tests cover validation, transition rules, availability generation/exclusion, and endpoint behavior without Oracle. Reminders/notifications, waitlists, timezone conversion, and medical-record/billing integration remain future work.

## SQL execution order

For an existing schema, run `database/migrations/V007__appointment_management.sql` once as `HOSPITAL_APP`. A clean installation should run `schema.sql` and must not then rerun V007.
