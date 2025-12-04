package com.example.otomasyonogrenci.repository;

import com.example.otomasyonogrenci.model.Course;
import com.example.otomasyonogrenci.model.Enrollment;
import com.example.otomasyonogrenci.model.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface EnrollmentRepository extends JpaRepository<Enrollment, Long> {
    Optional<Enrollment> findByStudentAndCourse(User student, Course course);
    List<Enrollment> findByStudent(User student);
    List<Enrollment> findByStudentId(Long studentId);
    List<Enrollment> findByCourse(Course course);
    List<Enrollment> findByCourseId(Long courseId);
    List<Enrollment> findByStatus(Enrollment.EnrollmentStatus status);
    Long countByCourseAndStatus(Course course, Enrollment.EnrollmentStatus status);
}

