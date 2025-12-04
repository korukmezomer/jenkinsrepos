package com.example.otomasyonogrenci.service;

import com.example.otomasyonogrenci.model.Course;
import com.example.otomasyonogrenci.model.Enrollment;
import com.example.otomasyonogrenci.model.User;
import com.example.otomasyonogrenci.repository.CourseRepository;
import com.example.otomasyonogrenci.repository.EnrollmentRepository;
import com.example.otomasyonogrenci.repository.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class EnrollmentServiceTest {

    @Mock
    private EnrollmentRepository enrollmentRepository;

    @Mock
    private UserRepository userRepository;

    @Mock
    private CourseRepository courseRepository;

    @InjectMocks
    private EnrollmentService enrollmentService;

    private User student;
    private Course course;
    private Enrollment enrollment;

    @BeforeEach
    void setUp() {
        student = new User();
        student.setId(1L);
        student.setUsername("student1");

        course = new Course();
        course.setId(1L);
        course.setCourseCode("CS101");
        course.setQuota(50);

        enrollment = new Enrollment();
        enrollment.setId(1L);
        enrollment.setStudent(student);
        enrollment.setCourse(course);
        enrollment.setStatus(Enrollment.EnrollmentStatus.ACTIVE);
    }

    @Test
    void testEnrollStudent() {
        when(userRepository.findById(1L)).thenReturn(Optional.of(student));
        when(courseRepository.findById(1L)).thenReturn(Optional.of(course));
        when(enrollmentRepository.findByStudentAndCourse(student, course)).thenReturn(Optional.empty());
        when(enrollmentRepository.countByCourseAndStatus(course, Enrollment.EnrollmentStatus.ACTIVE)).thenReturn(10L);
        when(enrollmentRepository.save(any(Enrollment.class))).thenReturn(enrollment);

        Enrollment result = enrollmentService.enrollStudent(1L, 1L);

        assertThat(result).isNotNull();
        verify(enrollmentRepository, times(1)).save(any(Enrollment.class));
    }

    @Test
    void testEnrollStudentAlreadyEnrolled() {
        when(userRepository.findById(1L)).thenReturn(Optional.of(student));
        when(courseRepository.findById(1L)).thenReturn(Optional.of(course));
        when(enrollmentRepository.findByStudentAndCourse(student, course)).thenReturn(Optional.of(enrollment));

        assertThrows(RuntimeException.class, () -> {
            enrollmentService.enrollStudent(1L, 1L);
        });
    }

    @Test
    void testEnrollStudentQuotaFull() {
        when(userRepository.findById(1L)).thenReturn(Optional.of(student));
        when(courseRepository.findById(1L)).thenReturn(Optional.of(course));
        when(enrollmentRepository.findByStudentAndCourse(student, course)).thenReturn(Optional.empty());
        when(enrollmentRepository.countByCourseAndStatus(course, Enrollment.EnrollmentStatus.ACTIVE)).thenReturn(50L);

        assertThrows(RuntimeException.class, () -> {
            enrollmentService.enrollStudent(1L, 1L);
        });
    }

    @Test
    void testDropCourse() {
        when(enrollmentRepository.findById(1L)).thenReturn(Optional.of(enrollment));
        when(enrollmentRepository.save(any(Enrollment.class))).thenReturn(enrollment);

        Enrollment result = enrollmentService.dropCourse(1L);

        assertThat(result.getStatus()).isEqualTo(Enrollment.EnrollmentStatus.DROPPED);
        verify(enrollmentRepository, times(1)).save(any(Enrollment.class));
    }
}

