package com.example.otomasyonogrenci.controller;

import com.example.otomasyonogrenci.model.Course;
import com.example.otomasyonogrenci.model.Enrollment;
import com.example.otomasyonogrenci.model.Grade;
import com.example.otomasyonogrenci.service.CourseService;
import com.example.otomasyonogrenci.service.EnrollmentService;
import com.example.otomasyonogrenci.service.GradeService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@CrossOrigin(origins = "*", maxAge = 3600)
@RestController
@RequestMapping("/api/teacher")
@PreAuthorize("hasRole('TEACHER') or hasRole('ADMIN')")
public class TeacherController {
    @Autowired
    private CourseService courseService;

    @Autowired
    private EnrollmentService enrollmentService;

    @Autowired
    private GradeService gradeService;

    @GetMapping("/courses/{teacherId}")
    public ResponseEntity<List<Course>> getMyCourses(@PathVariable Long teacherId) {
        return ResponseEntity.ok(courseService.getCoursesByTeacher(teacherId));
    }

    @GetMapping("/courses/{courseId}/enrollments")
    public ResponseEntity<List<Enrollment>> getCourseEnrollments(@PathVariable Long courseId) {
        return ResponseEntity.ok(enrollmentService.getEnrollmentsByCourse(courseId));
    }

    @GetMapping("/courses/{courseId}/grades")
    public ResponseEntity<List<Grade>> getCourseGrades(@PathVariable Long courseId) {
        return ResponseEntity.ok(gradeService.getGradesByCourse(courseId));
    }

    @PostMapping("/courses")
    public ResponseEntity<Course> createCourse(@RequestBody Course course, @RequestParam Long teacherId) {
        try {
            Course createdCourse = courseService.createCourse(course, teacherId);
            return ResponseEntity.status(HttpStatus.CREATED).body(createdCourse);
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().build();
        }
    }

    @PutMapping("/courses/{id}")
    public ResponseEntity<Course> updateCourse(@PathVariable Long id, @RequestBody Course courseDetails) {
        try {
            Course updatedCourse = courseService.updateCourse(id, courseDetails);
            return ResponseEntity.ok(updatedCourse);
        } catch (RuntimeException e) {
            return ResponseEntity.notFound().build();
        }
    }

    @PostMapping("/grades")
    public ResponseEntity<Grade> enterGrade(
            @RequestParam Long enrollmentId,
            @RequestParam(required = false) Double midtermGrade,
            @RequestParam(required = false) Double finalGrade) {
        try {
            Grade grade = gradeService.createOrUpdateGrade(enrollmentId, midtermGrade, finalGrade);
            return ResponseEntity.status(HttpStatus.CREATED).body(grade);
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().build();
        }
    }
}

