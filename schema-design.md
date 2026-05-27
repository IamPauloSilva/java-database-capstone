# Smart Clinic Schema Design

This document defines an initial database design for the Smart Clinic Management System using both MySQL and MongoDB.

## MySQL Database Design

### Table: admin
- id: INT, Primary Key, Auto Increment
- username: VARCHAR(50), Not Null, Unique
- email: VARCHAR(120), Not Null, Unique
- password_hash: VARCHAR(255), Not Null
- role: VARCHAR(30), Not Null, Default 'admin'
- created_at: DATETIME, Not Null, Default CURRENT_TIMESTAMP
- updated_at: DATETIME, Not Null, Default CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP

### Table: patients
- id: INT, Primary Key, Auto Increment
- first_name: VARCHAR(80), Not Null
- last_name: VARCHAR(80), Not Null
- email: VARCHAR(120), Not Null, Unique
- phone: VARCHAR(20), Null
- date_of_birth: DATE, Not Null
- gender: VARCHAR(20), Null
- emergency_contact_name: VARCHAR(120), Null
- emergency_contact_phone: VARCHAR(20), Null
- created_at: DATETIME, Not Null, Default CURRENT_TIMESTAMP
- updated_at: DATETIME, Not Null, Default CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP

### Table: doctors
- id: INT, Primary Key, Auto Increment
- first_name: VARCHAR(80), Not Null
- last_name: VARCHAR(80), Not Null
- email: VARCHAR(120), Not Null, Unique
- phone: VARCHAR(20), Null
- specialization: VARCHAR(120), Not Null
- license_number: VARCHAR(50), Not Null, Unique
- years_experience: INT, Null
- status: TINYINT, Not Null, Default 1  (1 = active, 0 = inactive)
- created_at: DATETIME, Not Null, Default CURRENT_TIMESTAMP
- updated_at: DATETIME, Not Null, Default CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP

### Table: clinic_locations
- id: INT, Primary Key, Auto Increment
- name: VARCHAR(120), Not Null
- address_line1: VARCHAR(150), Not Null
- address_line2: VARCHAR(150), Null
- city: VARCHAR(80), Not Null
- state: VARCHAR(80), Not Null
- postal_code: VARCHAR(20), Not Null
- country: VARCHAR(80), Not Null
- phone: VARCHAR(20), Null

### Table: appointments
- id: INT, Primary Key, Auto Increment
- patient_id: INT, Not Null, Foreign Key -> patients(id) ON DELETE RESTRICT
- doctor_id: INT, Not Null, Foreign Key -> doctors(id) ON DELETE RESTRICT
- clinic_location_id: INT, Null, Foreign Key -> clinic_locations(id) ON DELETE SET NULL
- appointment_start: DATETIME, Not Null
- appointment_end: DATETIME, Not Null
- status: TINYINT, Not Null, Default 0  (0 = scheduled, 1 = completed, 2 = cancelled, 3 = no_show)
- reason: VARCHAR(255), Null
- created_by_admin_id: INT, Null, Foreign Key -> admin(id) ON DELETE SET NULL
- created_at: DATETIME, Not Null, Default CURRENT_TIMESTAMP
- updated_at: DATETIME, Not Null, Default CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP
- Constraint: CHECK (appointment_end > appointment_start)
- Index: idx_appointments_doctor_time (doctor_id, appointment_start)
- Index: idx_appointments_patient_time (patient_id, appointment_start)

### Table: doctor_availability
- id: INT, Primary Key, Auto Increment
- doctor_id: INT, Not Null, Foreign Key -> doctors(id) ON DELETE CASCADE
- weekday: TINYINT, Not Null  (0 = Sunday ... 6 = Saturday)
- start_time: TIME, Not Null
- end_time: TIME, Not Null
- is_available: TINYINT, Not Null, Default 1
- Constraint: CHECK (end_time > start_time)
- Unique: unique_doctor_weekday_slot (doctor_id, weekday, start_time, end_time)

### Table: payments
- id: INT, Primary Key, Auto Increment
- appointment_id: INT, Not Null, Foreign Key -> appointments(id) ON DELETE RESTRICT
- amount: DECIMAL(10,2), Not Null
- currency: CHAR(3), Not Null, Default 'USD'
- payment_method: VARCHAR(30), Not Null
- payment_status: TINYINT, Not Null, Default 0  (0 = pending, 1 = paid, 2 = failed, 3 = refunded)
- transaction_reference: VARCHAR(120), Null, Unique
- paid_at: DATETIME, Null
- created_at: DATETIME, Not Null, Default CURRENT_TIMESTAMP

### Design Notes
- Core transactional entities (patients, doctors, appointments, payments) are in MySQL for strong consistency and relational integrity.
- Patient/doctor deletion is restricted when related appointments exist, preserving medical history.
- Appointment overlap prevention should be enforced in service logic and/or transaction-safe checks using `(doctor_id, appointment_start, appointment_end)`.
- Email/phone format validation should be enforced in application code and API validation layers.

## MongoDB Collection Design

### Collection: clinical_notes

```json
{
  "_id": "ObjectId('6841f1234abcde0000012345')",
  "appointmentId": 1024,
  "patientId": 87,
  "doctorId": 15,
  "noteType": "consultation_summary",
  "summary": "Patient reports intermittent migraines for 2 weeks.",
  "observations": [
    {
      "key": "blood_pressure",
      "value": "128/82",
      "recordedAt": "2026-05-27T09:10:00Z"
    },
    {
      "key": "temperature_c",
      "value": 36.8,
      "recordedAt": "2026-05-27T09:11:00Z"
    }
  ],
  "prescriptions": [
    {
      "medication": "Ibuprofen",
      "dosage": "400mg",
      "instructions": "Take 1 tablet after meals when needed",
      "durationDays": 5
    }
  ],
  "attachments": [
    {
      "fileName": "lab_result_2026_05_20.pdf",
      "fileUrl": "https://files.smartclinic.example/labs/abc123.pdf",
      "contentType": "application/pdf"
    }
  ],
  "tags": ["neurology", "follow-up"],
  "metadata": {
    "source": "doctor_portal",
    "version": 2
  },
  "createdAt": "2026-05-27T09:15:00Z",
  "updatedAt": "2026-05-27T09:20:00Z"
}
```

### MongoDB Design Notes
- This collection stores flexible, semi-structured clinical content that can evolve without rigid schema migrations.
- Documents reference MySQL IDs (`appointmentId`, `patientId`, `doctorId`) instead of embedding full relational records.
- Embedded arrays (`observations`, `prescriptions`, `attachments`) support variable-length clinical data per appointment.
- Suggested indexes:
  - `{ "appointmentId": 1 }`
  - `{ "doctorId": 1, "createdAt": -1 }`
  - `{ "patientId": 1, "createdAt": -1 }`
