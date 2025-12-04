package com.example.otomasyonogrenci.controller;

import com.example.otomasyonogrenci.model.Enrollment;
import com.example.otomasyonogrenci.model.Grade;
import com.example.otomasyonogrenci.service.EnrollmentService;
import com.example.otomasyonogrenci.service.GradeService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@CrossOrigin(origins = "*", maxAge = 3600)
@RestController
@RequestMapping("/api/student")
@PreAuthorize("hasRole('STUDENT')")
public class StudentController {
    @Autowired
    private EnrollmentService enrollmentService;

    @Autowired
    private GradeService gradeService;

    @GetMapping("/enrollments")
    public ResponseEntity<List<Enrollment>> getMyEnrollments() {
        // Bu örnekte kullanıcı ID'sini authentication'dan alıyoruz
        // Gerçek uygulamada UserDetails'den alınmalı
        String username = SecurityContextHolder.getContext().getAuthentication().getName();
        // Burada username'den user ID bulunmalı, şimdilik örnek olarak bırakıyoruz
        // Long studentId = getCurrentUserId();
        // return ResponseEntity.ok(enrollmentService.getEnrollmentsByStudent(studentId));
        return ResponseEntity.ok(enrollmentService.getAllEnrollments());
    }

    @GetMapping("/enrollments/{studentId}")
    public ResponseEntity<List<Enrollment>> getEnrollmentsByStudentId(@PathVariable Long studentId) {
        return ResponseEntity.ok(enrollmentService.getEnrollmentsByStudent(studentId));
    }

    @GetMapping("/grades/{studentId}")
    public ResponseEntity<List<Grade>> getMyGrades(@PathVariable Long studentId) {
        return ResponseEntity.ok(gradeService.getGradesByStudent(studentId));
    }

    @PostMapping("/enroll")
    public ResponseEntity<Enrollment> enrollInCourse(
            @RequestParam Long studentId,
            @RequestParam Long courseId) {
        try {
            Enrollment enrollment = enrollmentService.enrollStudent(studentId, courseId);
            return ResponseEntity.ok(enrollment);
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().build();
        }
    }

    @PutMapping("/enrollments/{id}/drop")
    public ResponseEntity<Enrollment> dropMyCourse(@PathVariable Long id) {
        try {
            Enrollment enrollment = enrollmentService.dropCourse(id);
            return ResponseEntity.ok(enrollment);
        } catch (RuntimeException e) {
            return ResponseEntity.notFound().build();
        }
    }
}

