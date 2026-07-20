# Authentication and Authorization

Login is handled by `LoginServlet`, `AuthenticationService`, and `UserDao`. The username is normalized, the stored BCrypt hash is verified, and only `AuthenticatedUser` (never a hash) enters the session. A successful login invalidates the old session, creates a new 30-minute session, and redirects to the dashboard. Logout is POST-only in the UI, audited, and invalidates the session.

Passwords use BCrypt with a random salt and work factor 12. Five consecutive failures lock an account. Messages do not reveal whether an identity exists or include database errors. Login success/failure, lockout, logout, and denied access are written best-effort to `AUDIT_LOGS`; audit failure cannot block authentication.

`AuthenticationFilter` protects dashboard and role areas. `RoleAuthorizationFilter` enforces server-side access: ADMIN has admin access and is also accepted in every specialist area; DOCTOR, NURSE, RECEPTIONIST, PATIENT, and BILLING_STAFF have only their matching paths. Hiding dashboard cards is convenience, not authorization.

CSRF tokens are 256-bit SecureRandom values bound to the session and required for POST/PUT/PATCH/DELETE, including login and logout. Security headers disable caching, framing, MIME sniffing, and apply a CSP allowing local resources and the Bootstrap CDN. Session renewal prevents fixation.

Demo users are optional. Generate a separate BCrypt hash locally, replace each placeholder in `database/create-demo-users.sql`, run it as HOSPITAL_APP, then discard plaintext input. Never commit known credentials.

Limitations: there is no password reset, MFA, remember-me, rate limiting across nodes, centralized audit monitoring, or HTTPS enforcement in the application. Production deployment must use HTTPS, secure cookie settings at Tomcat/proxy level, secret management, stronger operational monitoring, and a controlled account-unlock process.
