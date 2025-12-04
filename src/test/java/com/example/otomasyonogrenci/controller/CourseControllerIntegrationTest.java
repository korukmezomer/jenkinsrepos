package com.example.otomasyonogrenci.controller;

import com.example.otomasyonogrenci.config.AbstractIntegrationTest;
import com.example.otomasyonogrenci.model.Course;
import com.example.otomasyonogrenci.model.Role;
import com.example.otomasyonogrenci.model.User;
import com.example.otomasyonogrenci.repository.CourseRepository;
import com.example.otomasyonogrenci.repository.RoleRepository;
import com.example.otomasyonogrenci.repository.UserRepository;
import com.example.otomasyonogrenci.security.jwt.JwtUtils;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.transaction.annotation.Transactional;

import java.util.HashSet;
import java.util.Set;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("test")
@Transactional
class CourseControllerIntegrationTest extends AbstractIntegrationTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    private CourseRepository courseRepository;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private RoleRepository roleRepository;

    @Autowired
    private JwtUtils jwtUtils;

    @Autowired
    private UserDetailsService userDetailsService;

    private String teacherToken;
    private User teacher;
    private Course testCourse;

    @BeforeEach
    void setUp() {
        // Create roles
        Role teacherRole = roleRepository.findByName(Role.ERole.ROLE_TEACHER)
                .orElseGet(() -> {
                    Role role = new Role();
                    role.setName(Role.ERole.ROLE_TEACHER);
                    return roleRepository.save(role);
                });

        // Create teacher user
        teacher = new User();
        teacher.setUsername("teacher1");
        teacher.setEmail("teacher@example.com");
        teacher.setPassword("password123");
        teacher.setFirstName("Teacher");
        teacher.setLastName("One");
        teacher.setDepartment("Computer Science");
        teacher.setRoles(Set.of(teacherRole));
        teacher = userRepository.save(teacher);

        // Generate JWT token for teacher
        UserDetails userDetails = userDetailsService.loadUserByUsername(teacher.getUsername());
        Authentication authentication = new UsernamePasswordAuthenticationToken(
                userDetails, null, userDetails.getAuthorities());
        teacherToken = jwtUtils.generateJwtToken(authentication);

        // Create test course
        testCourse = new Course();
        testCourse.setCourseCode("CS101");
        testCourse.setCourseName("Introduction to Programming");
        testCourse.setDescription("Basic programming");
        testCourse.setCredit(3);
        testCourse.setQuota(50);
        testCourse.setTeacher(teacher);
        testCourse = courseRepository.save(testCourse);
    }

    @Test
    void testGetAllCourses() throws Exception {
        mockMvc.perform(get("/api/courses"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$").isArray());
    }

    @Test
    void testGetCourseById() throws Exception {
        mockMvc.perform(get("/api/courses/" + testCourse.getId()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.courseCode").value("CS101"));
    }

    @Test
    void testCreateCourse() throws Exception {
        Course newCourse = new Course();
        newCourse.setCourseCode("CS102");
        newCourse.setCourseName("Data Structures");
        newCourse.setDescription("Advanced data structures");
        newCourse.setCredit(4);
        newCourse.setQuota(30);

        mockMvc.perform(post("/api/courses?teacherId=" + teacher.getId())
                        .header("Authorization", "Bearer " + teacherToken)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(newCourse)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.courseCode").value("CS102"));
    }

    @Test
    void testCreateCourseUnauthorized() throws Exception {
        Course newCourse = new Course();
        newCourse.setCourseCode("CS103");
        newCourse.setCourseName("Unauthorized Course");
        newCourse.setCredit(3);
        newCourse.setQuota(20);

        mockMvc.perform(post("/api/courses?teacherId=1")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(newCourse)))
                .andExpect(status().isUnauthorized());
    }
}

