package com.example.otomasyonogrenci.repository;

import com.example.otomasyonogrenci.config.AbstractIntegrationTest;
import com.example.otomasyonogrenci.model.Role;
import com.example.otomasyonogrenci.model.User;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;
import org.springframework.test.context.ActiveProfiles;

import java.util.HashSet;
import java.util.Optional;
import java.util.Set;

import static org.assertj.core.api.Assertions.assertThat;

@DataJpaTest
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
@ActiveProfiles("test")
class UserRepositoryTest extends AbstractIntegrationTest {

    @Autowired
    private TestEntityManager entityManager;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private RoleRepository roleRepository;

    private Role studentRole;
    private User testUser;

    @BeforeEach
    void setUp() {
        // Create roles
        studentRole = new Role();
        studentRole.setName(Role.ERole.ROLE_STUDENT);
        entityManager.persistAndFlush(studentRole);

        // Create test user
        testUser = new User();
        testUser.setUsername("testuser");
        testUser.setEmail("test@example.com");
        testUser.setPassword("password123");
        testUser.setFirstName("Test");
        testUser.setLastName("User");
        testUser.setStudentNumber("2024001");
        
        Set<Role> roles = new HashSet<>();
        roles.add(studentRole);
        testUser.setRoles(roles);
        
        entityManager.persistAndFlush(testUser);
    }

    @Test
    void testFindByUsername() {
        Optional<User> found = userRepository.findByUsername("testuser");
        assertThat(found).isPresent();
        assertThat(found.get().getUsername()).isEqualTo("testuser");
    }

    @Test
    void testFindByEmail() {
        Optional<User> found = userRepository.findByEmail("test@example.com");
        assertThat(found).isPresent();
        assertThat(found.get().getEmail()).isEqualTo("test@example.com");
    }

    @Test
    void testExistsByUsername() {
        assertThat(userRepository.existsByUsername("testuser")).isTrue();
        assertThat(userRepository.existsByUsername("nonexistent")).isFalse();
    }

    @Test
    void testExistsByEmail() {
        assertThat(userRepository.existsByEmail("test@example.com")).isTrue();
        assertThat(userRepository.existsByEmail("nonexistent@example.com")).isFalse();
    }

    @Test
    void testFindByRoles_Name() {
        var users = userRepository.findByRoles_Name(Role.ERole.ROLE_STUDENT);
        assertThat(users).isNotEmpty();
        assertThat(users).anyMatch(u -> u.getUsername().equals("testuser"));
    }

    @Test
    void testFindByStudentNumberIsNotNull() {
        var students = userRepository.findByStudentNumberIsNotNull();
        assertThat(students).isNotEmpty();
        assertThat(students).anyMatch(u -> u.getStudentNumber().equals("2024001"));
    }
}

