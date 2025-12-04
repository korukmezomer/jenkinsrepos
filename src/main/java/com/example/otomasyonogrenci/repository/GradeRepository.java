package com.example.otomasyonogrenci.repository;

import com.example.otomasyonogrenci.model.Enrollment;
import com.example.otomasyonogrenci.model.Grade;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface GradeRepository extends JpaRepository<Grade, Long> {
    Optional<Grade> findByEnrollment(Enrollment enrollment);
    Optional<Grade> findByEnrollmentId(Long enrollmentId);
    List<Grade> findByEnrollment_StudentId(Long studentId);
    List<Grade> findByEnrollment_CourseId(Long courseId);
}

