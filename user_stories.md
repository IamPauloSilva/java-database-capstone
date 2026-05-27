# User Story Template

**Title:**
_As a [user role], I want [feature/goal], so that [reason]._

**Acceptance Criteria:**
1. [Criteria 1]
2. [Criteria 2]
3. [Criteria 3]

**Priority:** [High/Medium/Low]
**Story Points:** [Estimated Effort in Points]
**Notes:**
- [Additional information or edge cases]

---

## Admin User Stories

## Issue 1
**Title:**
_As an admin, I want to log into the portal with my username and password, so that I can manage the platform securely._

**Acceptance Criteria:**
1. Admin can access a login page with username and password fields.
2. Valid credentials authenticate the admin and redirect to the admin dashboard.
3. Invalid credentials display an error message and deny access.

**Priority:** High
**Story Points:** 3
**Notes:**
- Session should be created only after successful authentication.

## Issue 2
**Title:**
_As an admin, I want to log out of the portal, so that I can protect system access._

**Acceptance Criteria:**
1. Admin can trigger logout from the portal UI.
2. Logout invalidates the active session/token.
3. After logout, protected pages require login again.

**Priority:** High
**Story Points:** 2
**Notes:**
- Redirect admin to login page after logout.

## Issue 3
**Title:**
_As an admin, I want to add doctors to the portal, so that new doctors can be available to patients._

**Acceptance Criteria:**
1. Admin can open a create-doctor form.
2. Required doctor fields are validated before submission.
3. Successfully created doctor appears in the doctor list.

**Priority:** High
**Story Points:** 5
**Notes:**
- Ensure duplicate email or license number handling.

## Issue 4
**Title:**
_As an admin, I want to delete a doctor's profile from the portal, so that I can maintain accurate provider records._

**Acceptance Criteria:**
1. Admin can select a doctor profile and request deletion.
2. System asks for confirmation before permanent deletion.
3. Deleted profile no longer appears in active doctor listings.

**Priority:** Medium
**Story Points:** 3
**Notes:**
- Define behavior for linked future appointments before deletion.

## Issue 5
**Title:**
_As an admin, I want to run a stored procedure in MySQL CLI to get the number of appointments per month, so that I can track usage statistics._

**Acceptance Criteria:**
1. Stored procedure returns monthly appointment counts.
2. Admin can execute the procedure through MySQL CLI with documented command.
3. Output includes month and total appointments in readable format.

**Priority:** Medium
**Story Points:** 5
**Notes:**
- Document required DB user permissions for procedure execution.

---

## Patient User Stories

## Issue 1
**Title:**
_As a patient, I want to view a list of doctors without logging in, so that I can explore options before registering._

**Acceptance Criteria:**
1. Public users can access the doctor listing page.
2. List displays key doctor details (name, specialization, availability summary).
3. No login prompt is required to browse the list.

**Priority:** High
**Story Points:** 3
**Notes:**
- Hide sensitive doctor/private data from unauthenticated users.

## Issue 2
**Title:**
_As a patient, I want to sign up using my email and password, so that I can book appointments._

**Acceptance Criteria:**
1. Signup form accepts email and password inputs.
2. System validates email format and password policy.
3. Successful signup creates a patient account and confirms registration.

**Priority:** High
**Story Points:** 3
**Notes:**
- Email uniqueness must be enforced.

## Issue 3
**Title:**
_As a patient, I want to log into the portal, so that I can manage my bookings._

**Acceptance Criteria:**
1. Patient can log in with registered credentials.
2. Successful login grants access to booking management features.
3. Failed login displays an error and keeps account secure.

**Priority:** High
**Story Points:** 3
**Notes:**
- Apply account lockout/throttling rules if available.

## Issue 4
**Title:**
_As a patient, I want to log out of the portal, so that I can secure my account._

**Acceptance Criteria:**
1. Patient can log out from authenticated pages.
2. Logout clears authentication session/token.
3. Accessing protected pages after logout requires login.

**Priority:** High
**Story Points:** 2
**Notes:**
- Keep behavior consistent across desktop/mobile clients.

## Issue 5
**Title:**
_As a patient, I want to log in and book an hour-long appointment, so that I can consult with a doctor._

**Acceptance Criteria:**
1. Logged-in patients can select doctor, date, and one-hour time slot.
2. System prevents booking overlapping or unavailable slots.
3. Confirmed appointment is saved and shown in patient appointments.

**Priority:** High
**Story Points:** 5
**Notes:**
- Time zone handling must be defined for slot selection.

## Issue 6
**Title:**
_As a patient, I want to view my upcoming appointments, so that I can prepare accordingly._

**Acceptance Criteria:**
1. Patient can open an upcoming appointments view.
2. Each appointment shows doctor, date, time, and status.
3. Only future appointments are listed in this view.

**Priority:** Medium
**Story Points:** 3
**Notes:**
- Sort results by nearest upcoming date/time.

---

## Doctor User Stories

## Issue 1
**Title:**
_As a doctor, I want to log into the portal, so that I can manage my appointments._

**Acceptance Criteria:**
1. Doctor can authenticate with valid credentials.
2. Successful login redirects to doctor-specific dashboard.
3. Invalid login attempts show clear error feedback.

**Priority:** High
**Story Points:** 3
**Notes:**
- Enforce role-based access after authentication.

## Issue 2
**Title:**
_As a doctor, I want to log out of the portal, so that I can protect my data._

**Acceptance Criteria:**
1. Doctor can initiate logout from the portal UI.
2. Session/token is invalidated immediately on logout.
3. Returning to protected pages requires fresh login.

**Priority:** High
**Story Points:** 2
**Notes:**
- Ensure logout works from all doctor pages.

## Issue 3
**Title:**
_As a doctor, I want to view my appointment calendar, so that I can stay organized._

**Acceptance Criteria:**
1. Doctor can access calendar view of scheduled appointments.
2. Calendar displays appointments by date and time.
3. Calendar reflects new bookings or updates without stale data.

**Priority:** High
**Story Points:** 5
**Notes:**
- Define day/week/month calendar views based on UI scope.

## Issue 4
**Title:**
_As a doctor, I want to mark my unavailability, so that patients only see available slots._

**Acceptance Criteria:**
1. Doctor can add unavailability periods in schedule settings.
2. Unavailable periods block patient booking for those slots.
3. Existing calendar view clearly indicates unavailable times.

**Priority:** High
**Story Points:** 5
**Notes:**
- Validate conflicts with existing confirmed appointments.

## Issue 5
**Title:**
_As a doctor, I want to update my profile with specialization and contact information, so that patients have up-to-date information._

**Acceptance Criteria:**
1. Doctor can edit specialization and contact fields.
2. Profile updates are validated and saved successfully.
3. Updated information is visible in patient-facing doctor profiles.

**Priority:** Medium
**Story Points:** 3
**Notes:**
- Track last-updated timestamp for auditability.

## Issue 6
**Title:**
_As a doctor, I want to view patient details for upcoming appointments, so that I can be prepared._

**Acceptance Criteria:**
1. Doctor can open details for each upcoming appointment.
2. Details include patient name and relevant booking metadata.
3. Access is restricted to appointments assigned to that doctor.

**Priority:** Medium
**Story Points:** 3
**Notes:**
- Respect privacy rules for sensitive patient fields.
