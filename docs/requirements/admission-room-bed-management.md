# Admission, Room and Bed Management

This module manages rooms, beds, patient admission, transfer, discharge and cancellation. ADMIN and RECEPTIONIST perform lifecycle operations. Assigned DOCTOR users, NURSE, BILLING_STAFF and owning PATIENT users receive role-appropriate read-only access.

Rooms contain a type, floor, optional department and `BigDecimal` daily rate. Beds are unique within a room and can be AVAILABLE, OCCUPIED, MAINTENANCE or INACTIVE. Occupancy cannot be changed manually; admission transactions own that state.

Admission numbers use `ADMISSION_NUMBER_SEQ` and format `ADM-YYYY-NNNNNN`. Oracle conditional unique indexes enforce one active admission per patient and one active admission per bed. Creation locks the selected bed, inserts the admission and marks the bed OCCUPIED in one transaction.

Transfers lock the admission, current bed and target bed. They release the current bed, occupy the target, update the admission and insert immutable `ADMISSION_TRANSFERS` history atomically. The admission remains ADMITTED; `ADMISSION_TRANSFERRED` records the event.

Discharge locks the admission and bed, records the summary/timestamp and releases the bed atomically. Any started 24-hour interval counts as one day; room charge is `chargeable days × daily rate`, using scale 2 and HALF_UP. Cancellation similarly preserves the admission and releases its bed without deleting data.

Prepared statements, service-side ownership, CSRF-protected POST changes, row locking, safe errors and metadata-only audits protect the module. Clinical reason and discharge text are never copied into audit events. Events include ROOM/BED creation and status changes plus ADMISSION_CREATED, UPDATED, TRANSFERRED, DISCHARGED, CANCELLED and blocked/access-denied variants.

Known limitations: nursing notes, laboratory workflows, inventory, insurance, advanced ward history and partial-stay tariffs are outside scope. Existing legacy admissions require manual mapping before applying NOT NULL constraints after V011.
