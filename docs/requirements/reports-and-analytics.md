# Reports and Analytics

## Purpose and access

The module gives ADMIN users read-only operational and financial aggregates. All routes under `/admin/reports` are server-authorized for ADMIN only. Reports never modify hospital business data.

## Categories

- Hospital overview: departments, active doctors and patients, period appointments, active admissions, beds, billed, paid, and outstanding totals.
- Appointment: status totals, daily trend, and department/doctor aggregates.
- Patient: daily registration trend without addresses, contacts, or private identifiers.
- Doctor and department: total/active doctors and period appointment counts in one aggregate query.
- Admission and occupancy: admission lifecycle totals and operational beds by room type.
- Billing and revenue: bill-status totals and daily billed, successful-payment, and refund amounts.

## Filters and date semantics

Missing dates default to the first day of the current month through today. Dates use ISO `yyyy-MM-dd`. Timestamp queries use an inclusive lower bound and an exclusive upper bound of the day after `dateTo`. Invalid IDs, statuses, dates, and reversed ranges produce field-level errors. Values are bound using `PreparedStatement`; arbitrary SQL is never accepted.

## Calculations

Active admissions are `ADMITTED` or `TRANSFERRED`. Operational beds exclude `INACTIVE` beds and include available, occupied, and maintenance beds. Occupancy is `occupied / operational × 100`, with maintenance displayed separately.

Billing status uses values stored on `BILLS`. Billed trend uses `BILL_DATE`; successful and refunded payment trends use `PAYMENT_DATE`. Only `SUCCESS` payments count as paid revenue. `REFUNDED` payments are reported separately and are not double-counted as successful revenue.

## Security and privacy

Reports exclude password hashes, failed-login details, sessions, CSRF values, addresses, emergency contacts, clinical narratives, prescription instructions, transaction references, and payment credentials. CSV output contains aggregates only, quotes commas/quotes/newlines, and prefixes values beginning with `=`, `+`, `-`, or `@` to prevent spreadsheet formula injection.

CSV exports create a `REPORT_EXPORTED` audit event containing only the report-type identifier, user ID, IP address, and user agent. Audit failure follows the existing non-blocking audit policy.

## Print and errors

Every specialized report supports `?print=true`, external print CSS/JavaScript, applied filters, generation time, and summary tables. SQL failures are logged server-side and become safe empty report results; technical details are not returned to the browser.

## Tests and limitations

JUnit 5 and Mockito cover parser validation, CSV security, service fallback behavior, DAO mapping/binding/SELECT-only behavior, servlet output, export behavior, and role denial. Unit tests do not require Oracle. Live Oracle results depend on the local schema and data and must be verified separately.

Future enhancements may include inventory, laboratory, insurance, notifications, online payment gateways, and advanced visualization.
