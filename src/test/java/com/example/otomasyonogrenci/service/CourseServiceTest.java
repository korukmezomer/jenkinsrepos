package com.example.otomasyonogrenci.service;

import com.example.otomasyonogrenci.model.Course;
import com.example.otomasyonogrenci.model.User;
import com.example.otomasyonogrenci.repository.CourseRepository;
import com.example.otomasyonogrenci.repository.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class CourseServiceTest {

    @Mock
    private CourseRepository courseRepository;

    @Mock
    private UserRepository userRepository;

    @InjectMocks
    private CourseService courseService;

    private Course testCourse;
    private User teacher;

    @BeforeEach
    void setUp() {
        teacher = new User();
        teacher.setId(1L);
        teacher.setUsername("teacher1");

        testCourse = new Course();
        testCourse.setId(1L);
        testCourse.setCourseCode("CS101");
        testCourse.setCourseName("Introduction to Programming");
        testCourse.setDescription("Basic programming");
        testCourse.setCredit(3);
        testCourse.setQuota(50);
        testCourse.setTeacher(teacher);
    }

    @Test
    void testGetAllCourses() {
        List<Course> courses = Arrays.asList(testCourse);
        when(courseRepository.findAll()).thenReturn(courses);

        List<Course> result = courseService.getAllCourses();

        assertThat(result).hasSize(1);
        assertThat(result.get(0).getCourseCode()).isEqualTo("CS101");
    }

    @Test
    void testGetCourseById() {
        when(courseRepository.findById(1L)).thenReturn(Optional.of(testCourse));

        Optional<Course> result = courseService.getCourseById(1L);

        assertThat(result).isPresent();
        assertThat(result.get().getCourseCode()).isEqualTo("CS101");
    }

    @Test
    void testCreateCourse() {
        Course newCourse = new Course();
        newCourse.setCourseCode("CS102");
        newCourse.setCourseName("Data Structures");
        newCourse.setCredit(4);
        newCourse.setQuota(30);

        when(userRepository.findById(1L)).thenReturn(Optional.of(teacher));
        when(courseRepository.save(any(Course.class))).thenReturn(newCourse);

        Course result = courseService.createCourse(newCourse, 1L);

        assertThat(result).isNotNull();
        assertThat(result.getCourseCode()).isEqualTo("CS102");
        verify(courseRepository, times(1)).save(any(Course.class));
    }

    @Test
    void testCreateCourseTeacherNotFound() {
        Course newCourse = new Course();
        when(userRepository.findById(1L)).thenReturn(Optional.empty());

        assertThrows(RuntimeException.class, () -> {
            courseService.createCourse(newCourse, 1L);
        });
    }

    @Test
    void testUpdateCourse() {
        Course updatedCourse = new Course();
        updatedCourse.setCourseName("Updated Course Name");
        updatedCourse.setDescription("Updated description");
        updatedCourse.setCredit(4);
        updatedCourse.setQuota(60);

        when(courseRepository.findById(1L)).thenReturn(Optional.of(testCourse));
        when(courseRepository.save(any(Course.class))).thenReturn(testCourse);

        Course result = courseService.updateCourse(1L, updatedCourse);

        assertThat(result).isNotNull();
        verify(courseRepository, times(1)).save(any(Course.class));
    }

    @Test
    void testDeleteCourse() {
        when(courseRepository.findById(1L)).thenReturn(Optional.of(testCourse));
        doNothing().when(courseRepository).delete(testCourse);

        courseService.deleteCourse(1L);

        verify(courseRepository, times(1)).delete(testCourse);
    }
}

