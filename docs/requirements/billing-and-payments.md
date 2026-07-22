# Billing and Payments Management

## Purpose and roles

The module creates itemized patient bills, records offline payments, retains payment history, and produces invoices and receipts. ADMIN and BILLING_STAFF manage billing; RECEPTIONIST may create/view bills and record CASH payments; PATIENT has read-only access to bills and receipts linked to their own patient profile. DOCTOR has no billing-management access.

## Structure and dependencies

`BILLS` stores the server-calculated summary and optional appointment link. `BILL_ITEMS` stores one or more consultation, room, medicine, lab-test, procedure, or other charges. `PAYMENTS` stores offline payment metadata. Appointment billing verifies that the appointment belongs to the selected patient and blocks a second non-cancelled bill. Manual patient billing is permitted for operational roles.

Bill numbers use `BILL_NUMBER_SEQ` and `BILL-YYYY-NNNNNN`; payment numbers use `PAYMENT_NUMBER_SEQ` and `PAY-YYYY-NNNNNN`. Row counts are never used.

## Calculations and transactions

- `lineTotal = quantity × unitPrice`
- `subtotal = sum(lineTotal)`
- `total = subtotal + tax - discount`
- `paid = sum(SUCCESS payments)`
- `balance = total - paid`

All money uses `BigDecimal`, scale 2, and `HALF_UP`. The server ignores client previews and recalculates every value. Bill header and item insertion/replacement share one JDBC transaction. An edit is permitted only while UNPAID with no successful payment.

Payment recording locks the bill row with `SELECT ... FOR UPDATE`, validates against the locked balance, inserts the payment, recalculates paid/balance/status, and updates the bill in one transaction. This prevents concurrent overpayment. Payments may be partial or full and cannot exceed balance.

ADMIN may refund a complete SUCCESS payment; partial refunds are outside scope. The payment becomes REFUNDED and is never deleted. Remaining successful payments derive PAID/PARTIALLY_PAID. When none remain after a refund, the bill becomes REFUNDED. Cancellation is allowed only for UNPAID bills without successful payments and preserves all data.

## Security and audit

Ownership is enforced in services through the authenticated user's linked patient profile. All state changes are POST requests protected by the existing CSRF filter. Prepared statements prevent SQL injection. The application collects only an external transaction reference—never card numbers, CVV, PIN, UPI PIN, bank passwords, or gateway credentials.

Audit events are `BILL_CREATED`, `BILL_UPDATED`, `BILL_CANCELLED`, `BILL_CREATION_BLOCKED`, `BILL_ACCESS_DENIED`, `PAYMENT_RECORDED`, `PAYMENT_REFUNDED`, `PAYMENT_BLOCKED`, and `PAYMENT_ACCESS_DENIED`. Events contain identity/request metadata and entity IDs, not item descriptions, complete financial payloads, or transaction references. Audit failure does not undo a completed business transaction.

## Errors, tests, and limitations

SQL errors are logged server-side and converted to safe service results. Unit tests cover calculations, rounding, validation, balance enforcement, and status transitions without Oracle. Insurance claim processing, partial refunds, online gateways, charge catalogues, and tax-rule engines are future work.

## Deployment

For an existing schema, run `database/migrations/V010__billing_and_payments.sql` as `HOSPITAL_APP` after V009, rebuild the WAR, deploy to Tomcat 9, then verify `/billing` with each supported role.
