package com.project.back_end.services;

import com.project.back_end.DTO.AppointmentDTO;
import com.project.back_end.DTO.Login;
import com.project.back_end.models.Admin;
import com.project.back_end.models.Doctor;
import com.project.back_end.models.Patient;
import com.project.back_end.repo.AdminRepository;
import com.project.back_end.repo.DoctorRepository;
import com.project.back_end.repo.PatientRepository;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

@org.springframework.stereotype.Service
public class Service {
    private final TokenService tokenService;
    private final AdminRepository adminRepository;
    private final DoctorRepository doctorRepository;
    private final PatientRepository patientRepository;
    private final DoctorService doctorService;
    private final PatientService patientService;

    public Service(TokenService tokenService,
                   AdminRepository adminRepository,
                   DoctorRepository doctorRepository,
                   PatientRepository patientRepository,
                   DoctorService doctorService,
                   PatientService patientService) {
        this.tokenService = tokenService;
        this.adminRepository = adminRepository;
        this.doctorRepository = doctorRepository;
        this.patientRepository = patientRepository;
        this.doctorService = doctorService;
        this.patientService = patientService;
    }

    public ResponseEntity<Map<String, Object>> validateToken(String token, String user) {
        if (token == null || token.isBlank() || !tokenService.validateToken(token, user)) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(Map.of("message", "invalid or expired token"));
        }
        return ResponseEntity.ok(new HashMap<>());
    }

    public ResponseEntity<Map<String, Object>> validateAdmin(Admin admin) {
        try {
            Admin current = adminRepository.findByUsername(admin.getUsername());
            if (current == null || !current.getPassword().equals(admin.getPassword())) {
                return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(Map.of("message", "invalid credentials"));
            }
            String token = tokenService.generateToken(current.getUsername());
            return ResponseEntity.ok(Map.of("message", "login successful", "token", token));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(Map.of("message", "internal server error"));
        }
    }

    public List<Doctor> filterDoctor(String name, String time, String specialty) {
        String n = name == null ? "" : name;
        String t = time == null ? "" : time;
        String s = specialty == null ? "" : specialty;

        if (!n.isBlank() && !t.isBlank() && !s.isBlank()) {
            return doctorService.filterDoctorsByNameSpecilityandTime(n, s, t);
        }
        if (!n.isBlank() && !t.isBlank()) {
            return doctorService.filterDoctorByNameAndTime(n, t);
        }
        if (!n.isBlank() && !s.isBlank()) {
            return doctorService.filterDoctorByNameAndSpecility(n, s);
        }
        if (!t.isBlank() && !s.isBlank()) {
            return doctorService.filterDoctorByTimeAndSpecility(t, s);
        }
        if (!n.isBlank()) {
            return doctorService.findDoctorByName(n);
        }
        if (!t.isBlank()) {
            return doctorService.filterDoctorsByTime(t);
        }
        if (!s.isBlank()) {
            return doctorService.filterDoctorBySpecility(s);
        }
        return doctorService.getDoctors();
    }

    public int validateAppointment(Long doctorId, LocalDateTime appointmentTime) {
        Doctor doctor = doctorRepository.findById(doctorId).orElse(null);
        if (doctor == null) {
            return -1;
        }
        List<String> available = doctorService.getDoctorAvailability(doctorId, appointmentTime.toLocalDate());
        String wanted = appointmentTime.toLocalTime().withSecond(0).withNano(0).toString();
        boolean match = available.stream().anyMatch(slot -> slot.startsWith(wanted));
        return match ? 1 : 0;
    }

    public boolean validatePatient(Patient patient) {
        return patientRepository.findByEmailOrPhone(patient.getEmail(), patient.getPhone()) == null;
    }

    public ResponseEntity<Map<String, Object>> validatePatientLogin(Login login) {
        try {
            Patient patient = patientRepository.findByEmail(login.getEmail());
            if (patient == null || !patient.getPassword().equals(login.getPassword())) {
                return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(Map.of("message", "invalid credentials"));
            }
            String token = tokenService.generateToken(patient.getEmail());
            return ResponseEntity.ok(Map.of("message", "login successful", "token", token));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(Map.of("message", "internal server error"));
        }
    }

    public ResponseEntity<Map<String, Object>> filterPatient(String token, String condition, String name) {
        try {
            String email = tokenService.extractEmail(token);
            Patient patient = patientRepository.findByEmail(email);
            if (patient == null) {
                return ResponseEntity.status(HttpStatus.NOT_FOUND).body(Map.of("message", "patient not found"));
            }

            List<AppointmentDTO> result;
            boolean hasCondition = condition != null && !condition.isBlank();
            boolean hasName = name != null && !name.isBlank();

            if (hasCondition && hasName) {
                result = patientService.filterByDoctorAndCondition(patient.getId(), name, condition);
            } else if (hasCondition) {
                result = patientService.filterByCondition(patient.getId(), condition);
            } else if (hasName) {
                result = patientService.filterByDoctor(patient.getId(), name);
            } else {
                result = patientService.getPatientAppointment(patient.getId());
            }
            return ResponseEntity.ok(Map.of("appointments", result));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(Map.of("message", "internal server error"));
        }
    }
}
