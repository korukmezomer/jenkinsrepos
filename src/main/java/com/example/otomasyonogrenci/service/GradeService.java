package com.example.otomasyonogrenci.service;

import com.example.otomasyonogrenci.model.Enrollment;
import com.example.otomasyonogrenci.model.Grade;
import com.example.otomasyonogrenci.repository.EnrollmentRepository;
import com.example.otomasyonogrenci.repository.GradeRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Service
public class GradeService {
    @Autowired
    private GradeRepository gradeRepository;

    @Autowired
    private EnrollmentRepository enrollmentRepository;

    public List<Grade> getAllGrades() {
        return gradeRepository.findAll();
    }

    public Optional<Grade> getGradeById(Long id) {
        return gradeRepository.findById(id);
    }

    public Optional<Grade> getGradeByEnrollment(Long enrollmentId) {
        return gradeRepository.findByEnrollmentId(enrollmentId);
    }

    public List<Grade> getGradesByStudent(Long studentId) {
        return gradeRepository.findByEnrollment_StudentId(studentId);
    }

    public List<Grade> getGradesByCourse(Long courseId) {
        return gradeRepository.findByEnrollment_CourseId(courseId);
    }

    @Transactional
    public Grade createOrUpdateGrade(Long enrollmentId, Double midtermGrade, Double finalGrade) {
        Enrollment enrollment = enrollmentRepository.findById(enrollmentId)
                .orElseThrow(() -> new RuntimeException("Enrollment not found"));

        Optional<Grade> existingGrade = gradeRepository.findByEnrollment(enrollment);

        Grade grade;
        if (existingGrade.isPresent()) {
            grade = existingGrade.get();
        } else {
            grade = new Grade();
            grade.setEnrollment(enrollment);
        }

        grade.setMidtermGrade(midtermGrade);
        grade.setFinalGrade(finalGrade);
        grade.calculateAverage();
        grade.setGradeDate(LocalDateTime.now());

        return gradeRepository.save(grade);
    }

    @Transactional
    public void deleteGrade(Long id) {
        Grade grade = gradeRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Grade not found"));
        gradeRepository.delete(grade);
    }
}

