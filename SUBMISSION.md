# Smart Clinic Capstone Submission

## Public Links

- Issues with user stories (Doctor, Patient, Admin):  
  `PASTE_GITHUB_ISSUES_LINK_HERE`
- `schema-design.md`:  
  `PASTE_SCHEMA_DESIGN_LINK_HERE`
- `Doctor.java`:  
  `PASTE_DOCTOR_JAVA_LINK_HERE`
- `Appointment.java`:  
  `PASTE_APPOINTMENT_JAVA_LINK_HERE`
- `DoctorController.java`:  
  `PASTE_DOCTOR_CONTROLLER_LINK_HERE`
- `AppointmentService.java`:  
  `PASTE_APPOINTMENT_SERVICE_LINK_HERE`
- `PrescriptionController.java`:  
  `PASTE_PRESCRIPTION_CONTROLLER_LINK_HERE`
- `PatientRepository.java`:  
  `PASTE_PATIENT_REPOSITORY_LINK_HERE`
- `TokenService.java`:  
  `PASTE_TOKEN_SERVICE_LINK_HERE`
- `DoctorService.java`:  
  `PASTE_DOCTOR_SERVICE_LINK_HERE`
- `Dockerfile`:  
  `PASTE_DOCKERFILE_LINK_HERE`
- GitHub Actions workflow (Java backend compile):  
  `PASTE_GITHUB_ACTIONS_WORKFLOW_LINK_HERE`

## Screenshots

- Admin portal login screen:  
  `PASTE_IMAGE_LINK_OR_ATTACH_IMAGE`
- Doctor portal login screen:  
  `PASTE_IMAGE_LINK_OR_ATTACH_IMAGE`
- Patient portal login screen:  
  `PASTE_IMAGE_LINK_OR_ATTACH_IMAGE`
- Admin adding doctor:  
  `PASTE_IMAGE_LINK_OR_ATTACH_IMAGE`
- Patient searching doctor by name:  
  `PASTE_IMAGE_LINK_OR_ATTACH_IMAGE`
- Doctor viewing all patient appointments:  
  `PASTE_IMAGE_LINK_OR_ATTACH_IMAGE`

## SQL Outputs

### 1) Show all tables

Command:
```sql
SHOW TABLES;
```

Output:
```text
PASTE_OUTPUT_HERE
```

### 2) Show exactly 5 records from Patient table

Command:
```sql
SELECT * FROM Patient LIMIT 5;
```

Output:
```text
PASTE_OUTPUT_HERE
```

### 3) Run stored procedure: GetDailyAppointmentReportByDoctor

Command:
```sql
CALL GetDailyAppointmentReportByDoctor(PASTE_DOCTOR_ID, 'YYYY-MM-DD');
```

Output:
```text
PASTE_OUTPUT_HERE
```

### 4) Run stored procedure: GetDoctorWithMostPatientsByMonth

Command:
```sql
CALL GetDoctorWithMostPatientsByMonth(PASTE_YEAR, PASTE_MONTH);
```

Output:
```text
PASTE_OUTPUT_HERE
```

### 5) Run stored procedure: GetDoctorWithMostPatientsByYear

Command:
```sql
CALL GetDoctorWithMostPatientsByYear(PASTE_YEAR);
```

Output:
```text
PASTE_OUTPUT_HERE
```

## cURL Outputs

### 1) GET all doctors

Command:
```bash
curl -X GET "http://localhost:8080/doctor"
```

Output:
```json
PASTE_OUTPUT_HERE
```

### 2) Get all appointments booked by a patient (using patient login credentials)

1. Login command:
```bash
curl -X POST "http://localhost:8080/patient/login" -H "Content-Type: application/json" -d "{\"email\":\"PATIENT_EMAIL\",\"password\":\"PATIENT_PASSWORD\"}"
```

2. Use token from login response:
```bash
curl -X GET "http://localhost:8080/patient/appointments/PATIENT_ID/PATIENT_TOKEN/patient"
```

Output:
```json
PASTE_OUTPUT_HERE
```

### 3) GET doctors by speciality and time

Command:
```bash
curl -X GET "http://localhost:8080/doctor/filter?speciality=Cardiologist&time=AM"
```

Output:
```json
PASTE_OUTPUT_HERE
```

---

## Final Validation Checklist

- [ ] All links open publicly in an incognito window.
- [ ] Screenshots are clear and show required action/state.
- [ ] SQL outputs match requested statements.
- [ ] cURL outputs are from your running backend and not placeholders.
- [ ] Commit hash/tag for final submission is recorded.
