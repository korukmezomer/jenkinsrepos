package com.example.otomasyonogrenci.repository;

import com.example.otomasyonogrenci.config.AbstractIntegrationTest;
import com.example.otomasyonogrenci.model.Course;
import com.example.otomasyonogrenci.model.Role;
import com.example.otomasyonogrenci.model.User;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;
import org.springframework.test.context.ActiveProfiles;

import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;

@DataJpaTest
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
@ActiveProfiles("test")
class CourseRepositoryTest extends AbstractIntegrationTest {

    @Autowired
    private TestEntityManager entityManager;

    @Autowired
    private CourseRepository courseRepository;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private RoleRepository roleRepository;

    private User teacher;
    private Course testCourse;

    @BeforeEach
    void setUp() {
        // Create teacher role
        Role teacherRole = new Role();
        teacherRole.setName(Role.ERole.ROLE_TEACHER);
        entityManager.persistAndFlush(teacherRole);

        // Create teacher user
        teacher = new User();
        teacher.setUsername("teacher1");
        teacher.setEmail("teacher@example.com");
        teacher.setPassword("password123");
        teacher.setFirstName("Teacher");
        teacher.setLastName("One");
        teacher.setDepartment("Computer Science");
        teacher.setRoles(java.util.Set.of(teacherRole));
        entityManager.persistAndFlush(teacher);

        // Create test course
        testCourse = new Course();
        testCourse.setCourseCode("CS101");
        testCourse.setCourseName("Introduction to Programming");
        testCourse.setDescription("Basic programming concepts");
        testCourse.setCredit(3);
        testCourse.setQuota(50);
        testCourse.setTeacher(teacher);
        entityManager.persistAndFlush(testCourse);
    }

    @Test
    void testFindByCourseCode() {
        Optional<Course> found = courseRepository.findByCourseCode("CS101");
        assertThat(found).isPresent();
        assertThat(found.get().getCourseName()).isEqualTo("Introduction to Programming");
    }

    @Test
    void testExistsByCourseCode() {
        assertThat(courseRepository.existsByCourseCode("CS101")).isTrue();
        assertThat(courseRepository.existsByCourseCode("CS999")).isFalse();
    }

    @Test
    void testFindByTeacher() {
        var courses = courseRepository.findByTeacher(teacher);
        assertThat(courses).isNotEmpty();
        assertThat(courses).anyMatch(c -> c.getCourseCode().equals("CS101"));
    }

    @Test
    void testFindByTeacherId() {
        var courses = courseRepository.findByTeacherId(teacher.getId());
        assertThat(courses).isNotEmpty();
        assertThat(courses).anyMatch(c -> c.getCourseCode().equals("CS101"));
    }
}

