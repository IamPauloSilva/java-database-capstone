package com.project.back_end.controllers;

import com.project.back_end.models.Appointment;
import com.project.back_end.services.AppointmentService;
import com.project.back_end.services.Service;
import jakarta.validation.Valid;
import java.time.LocalDate;
import java.util.Map;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("${api.path}appointments")
public class AppointmentController {
    private final AppointmentService appointmentService;
    private final Service service;

    public AppointmentController(AppointmentService appointmentService, Service service) {
        this.appointmentService = appointmentService;
        this.service = service;
    }

    @GetMapping("/{doctorId}/{date}/{token}")
    public ResponseEntity<Map<String, Object>> getAppointments(@PathVariable Long doctorId,
                                                               @PathVariable String date,
                                                               @PathVariable String token,
                                                               @RequestParam(required = false) String patientName) {
        ResponseEntity<Map<String, Object>> validation = service.validateToken(token, "doctor");
        if (!validation.getStatusCode().is2xxSuccessful()) {
            return validation;
        }
        return ResponseEntity.ok(Map.of("appointments", appointmentService.getAppointments(doctorId, LocalDate.parse(date), patientName)));
    }

    @PostMapping("/{token}")
    public ResponseEntity<Map<String, Object>> bookAppointment(@Valid @RequestBody Appointment appointment,
                                                                @PathVariable String token) {
        ResponseEntity<Map<String, Object>> validation = service.validateToken(token, "patient");
        if (!validation.getStatusCode().is2xxSuccessful()) {
            return validation;
        }

        int valid = service.validateAppointment(appointment.getDoctor().getId(), appointment.getAppointmentTime());
        if (valid == -1) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(Map.of("message", "doctor not found"));
        }
        if (valid == 0) {
            return ResponseEntity.status(HttpStatus.CONFLICT).body(Map.of("message", "doctor unavailable for this time"));
        }

        int booked = appointmentService.bookAppointment(appointment);
        if (booked == 1) {
            return ResponseEntity.status(HttpStatus.CREATED).body(Map.of("message", "appointment booked"));
        }
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(Map.of("message", "internal server error"));
    }

    @PutMapping("/{token}")
    public ResponseEntity<Map<String, Object>> updateAppointment(@Valid @RequestBody Appointment appointment,
                                                                  @PathVariable String token) {
        ResponseEntity<Map<String, Object>> validation = service.validateToken(token, "patient");
        if (!validation.getStatusCode().is2xxSuccessful()) {
            return validation;
        }
        return appointmentService.updateAppointment(appointment, token);
    }

    @DeleteMapping("/{appointmentId}/{token}")
    public ResponseEntity<Map<String, Object>> cancelAppointment(@PathVariable Long appointmentId, @PathVariable String token) {
        ResponseEntity<Map<String, Object>> validation = service.validateToken(token, "patient");
        if (!validation.getStatusCode().is2xxSuccessful()) {
            return validation;
        }
        return appointmentService.cancelAppointment(appointmentId, token);
    }
}
