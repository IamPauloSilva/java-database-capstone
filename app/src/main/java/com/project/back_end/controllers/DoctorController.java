package com.project.back_end.controllers;

import com.project.back_end.DTO.Login;
import com.project.back_end.models.Doctor;
import com.project.back_end.services.DoctorService;
import com.project.back_end.services.Service;
import jakarta.validation.Valid;
import java.time.LocalDate;
import java.util.List;
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
@RequestMapping("${api.path}doctor")
public class DoctorController {
    private final DoctorService doctorService;
    private final Service service;

    public DoctorController(DoctorService doctorService, Service service) {
        this.doctorService = doctorService;
        this.service = service;
    }

    @GetMapping("/availability/{user}/{doctorId}/{date}/{token}")
    public ResponseEntity<Map<String, Object>> getDoctorAvailability(@PathVariable String user,
                                                                     @PathVariable Long doctorId,
                                                                     @PathVariable String date,
                                                                     @PathVariable String token) {
        ResponseEntity<Map<String, Object>> validation = service.validateToken(token, user);
        if (!validation.getStatusCode().is2xxSuccessful()) {
            return validation;
        }
        List<String> slots = doctorService.getDoctorAvailability(doctorId, LocalDate.parse(date));
        return ResponseEntity.ok(Map.of("availability", slots));
    }

    @GetMapping
    public ResponseEntity<Map<String, Object>> getDoctor() {
        return ResponseEntity.ok(Map.of("doctors", doctorService.getDoctors()));
    }

    @PostMapping("/{token}")
    public ResponseEntity<Map<String, Object>> saveDoctor(@Valid @RequestBody Doctor doctor, @PathVariable String token) {
        ResponseEntity<Map<String, Object>> validation = service.validateToken(token, "admin");
        if (!validation.getStatusCode().is2xxSuccessful()) {
            return validation;
        }
        int status = doctorService.saveDoctor(doctor);
        if (status == -1) {
            return ResponseEntity.status(HttpStatus.CONFLICT).body(Map.of("message", "doctor already exists"));
        }
        if (status == 1) {
            return ResponseEntity.status(HttpStatus.CREATED).body(Map.of("message", "doctor created"));
        }
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(Map.of("message", "internal server error"));
    }

    @PostMapping("/login")
    public ResponseEntity<Map<String, Object>> doctorLogin(@Valid @RequestBody Login login) {
        return doctorService.validateDoctor(login);
    }

    @PutMapping("/{token}")
    public ResponseEntity<Map<String, Object>> updateDoctor(@Valid @RequestBody Doctor doctor, @PathVariable String token) {
        ResponseEntity<Map<String, Object>> validation = service.validateToken(token, "admin");
        if (!validation.getStatusCode().is2xxSuccessful()) {
            return validation;
        }
        int status = doctorService.updateDoctor(doctor);
        if (status == -1) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(Map.of("message", "doctor not found"));
        }
        if (status == 1) {
            return ResponseEntity.ok(Map.of("message", "doctor updated"));
        }
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(Map.of("message", "internal server error"));
    }

    @DeleteMapping("/{doctorId}/{token}")
    public ResponseEntity<Map<String, Object>> deleteDoctor(@PathVariable Long doctorId, @PathVariable String token) {
        ResponseEntity<Map<String, Object>> validation = service.validateToken(token, "admin");
        if (!validation.getStatusCode().is2xxSuccessful()) {
            return validation;
        }
        int status = doctorService.deleteDoctor(doctorId);
        if (status == -1) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(Map.of("message", "doctor not found"));
        }
        if (status == 1) {
            return ResponseEntity.ok(Map.of("message", "doctor deleted"));
        }
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(Map.of("message", "internal server error"));
    }

    @GetMapping("/filter")
    public ResponseEntity<Map<String, Object>> filter(@RequestParam(required = false) String name,
                                                       @RequestParam(required = false) String time,
                                                       @RequestParam(required = false) String speciality) {
        return ResponseEntity.ok(Map.of("doctors", service.filterDoctor(name, time, speciality)));
    }
}
