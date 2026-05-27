package com.project.back_end.services;

import com.project.back_end.models.Appointment;
import com.project.back_end.models.Patient;
import com.project.back_end.repo.AppointmentRepository;
import com.project.back_end.repo.PatientRepository;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class AppointmentService {
    private final AppointmentRepository appointmentRepository;
    private final com.project.back_end.services.Service commonService;
    private final TokenService tokenService;
    private final PatientRepository patientRepository;

    public AppointmentService(AppointmentRepository appointmentRepository,
                              com.project.back_end.services.Service commonService,
                              TokenService tokenService,
                              PatientRepository patientRepository) {
        this.appointmentRepository = appointmentRepository;
        this.commonService = commonService;
        this.tokenService = tokenService;
        this.patientRepository = patientRepository;
    }

    @Transactional
    public int bookAppointment(Appointment appointment) {
        try {
            appointmentRepository.save(appointment);
            return 1;
        } catch (Exception e) {
            return 0;
        }
    }

    @Transactional
    public ResponseEntity<Map<String, Object>> updateAppointment(Appointment payload, String token) {
        try {
            String email = tokenService.extractEmail(token);
            Patient currentPatient = patientRepository.findByEmail(email);
            if (currentPatient == null) {
                return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(Map.of("message", "invalid patient"));
            }

            Appointment existing = appointmentRepository.findById(payload.getId()).orElse(null);
            if (existing == null) {
                return ResponseEntity.status(HttpStatus.NOT_FOUND).body(Map.of("message", "appointment not found"));
            }
            if (!existing.getPatient().getId().equals(currentPatient.getId())) {
                return ResponseEntity.status(HttpStatus.FORBIDDEN).body(Map.of("message", "not allowed"));
            }

            int valid = commonService.validateAppointment(payload.getDoctor().getId(), payload.getAppointmentTime());
            if (valid == -1) {
                return ResponseEntity.status(HttpStatus.NOT_FOUND).body(Map.of("message", "doctor not found"));
            }
            if (valid == 0) {
                return ResponseEntity.status(HttpStatus.CONFLICT).body(Map.of("message", "doctor unavailable for this time"));
            }

            existing.setDoctor(payload.getDoctor());
            existing.setAppointmentTime(payload.getAppointmentTime());
            existing.setStatus(payload.getStatus());
            appointmentRepository.save(existing);
            return ResponseEntity.ok(Map.of("message", "appointment updated"));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(Map.of("message", "internal server error"));
        }
    }

    @Transactional
    public ResponseEntity<Map<String, Object>> cancelAppointment(Long appointmentId, String token) {
        try {
            String email = tokenService.extractEmail(token);
            Patient patient = patientRepository.findByEmail(email);
            if (patient == null) {
                return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(Map.of("message", "invalid patient"));
            }

            Appointment appointment = appointmentRepository.findById(appointmentId).orElse(null);
            if (appointment == null) {
                return ResponseEntity.status(HttpStatus.NOT_FOUND).body(Map.of("message", "appointment not found"));
            }
            if (!appointment.getPatient().getId().equals(patient.getId())) {
                return ResponseEntity.status(HttpStatus.FORBIDDEN).body(Map.of("message", "not allowed"));
            }
            appointmentRepository.delete(appointment);
            return ResponseEntity.ok(Map.of("message", "appointment cancelled"));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(Map.of("message", "internal server error"));
        }
    }

    @Transactional(readOnly = true)
    public List<Appointment> getAppointments(Long doctorId, LocalDate date, String patientName) {
        LocalDateTime start = date.atStartOfDay();
        LocalDateTime end = date.plusDays(1).atStartOfDay().minusNanos(1);
        if (patientName == null || patientName.isBlank()) {
            return appointmentRepository.findByDoctorIdAndAppointmentTimeBetween(doctorId, start, end);
        }
        return appointmentRepository.findByDoctorIdAndPatient_NameContainingIgnoreCaseAndAppointmentTimeBetween(
                doctorId,
                patientName,
                start,
                end
        );
    }

    @Transactional
    public void changeStatus(int status, long id) {
        appointmentRepository.updateStatus(status, id);
    }
}
