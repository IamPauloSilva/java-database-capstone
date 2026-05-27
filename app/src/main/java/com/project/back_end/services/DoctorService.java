package com.project.back_end.services;

import com.project.back_end.DTO.Login;
import com.project.back_end.models.Doctor;
import com.project.back_end.repo.AppointmentRepository;
import com.project.back_end.repo.DoctorRepository;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.*;
import java.util.stream.Collectors;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class DoctorService {
    private final DoctorRepository doctorRepository;
    private final AppointmentRepository appointmentRepository;
    private final TokenService tokenService;

    public DoctorService(DoctorRepository doctorRepository,
                         AppointmentRepository appointmentRepository,
                         TokenService tokenService) {
        this.doctorRepository = doctorRepository;
        this.appointmentRepository = appointmentRepository;
        this.tokenService = tokenService;
    }

    @Transactional(readOnly = true)
    public List<String> getDoctorAvailability(Long doctorId, LocalDate date) {
        Doctor doctor = doctorRepository.findById(doctorId).orElse(null);
        if (doctor == null) {
            return List.of();
        }
        LocalDateTime start = date.atStartOfDay();
        LocalDateTime end = date.plusDays(1).atStartOfDay().minusNanos(1);
        Set<String> bookedStarts = appointmentRepository.findByDoctorIdAndAppointmentTimeBetween(doctorId, start, end)
                .stream()
                .map(a -> a.getAppointmentTime().toLocalTime().withSecond(0).withNano(0).toString())
                .collect(Collectors.toSet());

        List<String> available = new ArrayList<>();
        for (String slot : doctor.getAvailableTimes()) {
            String[] parts = slot.split("-");
            if (parts.length > 0 && !bookedStarts.contains(parts[0])) {
                available.add(slot);
            }
        }
        available.sort(Comparator.naturalOrder());
        return available;
    }

    public int saveDoctor(Doctor doctor) {
        try {
            if (doctorRepository.findByEmail(doctor.getEmail()) != null) {
                return -1;
            }
            doctorRepository.save(doctor);
            return 1;
        } catch (Exception e) {
            return 0;
        }
    }

    public int updateDoctor(Doctor doctor) {
        try {
            if (doctor.getId() == null || !doctorRepository.existsById(doctor.getId())) {
                return -1;
            }
            doctorRepository.save(doctor);
            return 1;
        } catch (Exception e) {
            return 0;
        }
    }

    @Transactional(readOnly = true)
    public List<Doctor> getDoctors() {
        return doctorRepository.findAll();
    }

    public int deleteDoctor(Long doctorId) {
        try {
            if (!doctorRepository.existsById(doctorId)) {
                return -1;
            }
            appointmentRepository.deleteAllByDoctorId(doctorId);
            doctorRepository.deleteById(doctorId);
            return 1;
        } catch (Exception e) {
            return 0;
        }
    }

    public ResponseEntity<Map<String, Object>> validateDoctor(Login login) {
        try {
            Doctor doctor = doctorRepository.findByEmail(login.getEmail());
            if (doctor == null || !doctor.getPassword().equals(login.getPassword())) {
                return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(Map.of("message", "invalid credentials"));
            }
            String token = tokenService.generateToken(doctor.getEmail());
            return ResponseEntity.ok(Map.of("message", "login successful", "token", token));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(Map.of("message", "internal server error"));
        }
    }

    @Transactional(readOnly = true)
    public List<Doctor> findDoctorByName(String name) {
        return doctorRepository.findByNameLike("%" + name + "%");
    }

    public List<Doctor> filterDoctorsByNameSpecilityandTime(String name, String specialty, String time) {
        return filterDoctorByTime(doctorRepository.findByNameContainingIgnoreCaseAndSpecialtyIgnoreCase(name, specialty), time);
    }

    public List<Doctor> filterDoctorByTime(List<Doctor> doctors, String time) {
        String period = time == null ? "" : time.trim().toUpperCase();
        List<Doctor> result = new ArrayList<>();
        for (Doctor doctor : doctors) {
            boolean hasPeriod = doctor.getAvailableTimes().stream().anyMatch(slot -> isInPeriod(slot, period));
            if (hasPeriod) {
                result.add(doctor);
            }
        }
        return result;
    }

    public List<Doctor> filterDoctorByNameAndTime(String name, String time) {
        return filterDoctorByTime(findDoctorByName(name), time);
    }

    public List<Doctor> filterDoctorByNameAndSpecility(String name, String specialty) {
        return doctorRepository.findByNameContainingIgnoreCaseAndSpecialtyIgnoreCase(name, specialty);
    }

    public List<Doctor> filterDoctorByTimeAndSpecility(String time, String specialty) {
        return filterDoctorByTime(doctorRepository.findBySpecialtyIgnoreCase(specialty), time);
    }

    public List<Doctor> filterDoctorBySpecility(String specialty) {
        return doctorRepository.findBySpecialtyIgnoreCase(specialty);
    }

    public List<Doctor> filterDoctorsByTime(String time) {
        return filterDoctorByTime(doctorRepository.findAll(), time);
    }

    private boolean isInPeriod(String slot, String period) {
        if (period.isBlank()) {
            return true;
        }
        String[] parts = slot.split("-");
        if (parts.length == 0) {
            return false;
        }
        int hour = Integer.parseInt(parts[0].split(":")[0]);
        boolean am = hour < 12;
        if ("AM".equals(period)) {
            return am;
        }
        if ("PM".equals(period)) {
            return !am;
        }
        return true;
    }
}
