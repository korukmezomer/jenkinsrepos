package com.example.otomasyonogrenci.controller;

import com.example.otomasyonogrenci.config.AbstractIntegrationTest;
import com.example.otomasyonogrenci.dto.request.LoginRequest;
import com.example.otomasyonogrenci.dto.request.SignupRequest;
import com.example.otomasyonogrenci.model.Role;
import com.example.otomasyonogrenci.repository.RoleRepository;
import com.example.otomasyonogrenci.repository.UserRepository;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.transaction.annotation.Transactional;

import java.util.Set;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("test")
@Transactional
class AuthControllerIntegrationTest extends AbstractIntegrationTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private RoleRepository roleRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @BeforeEach
    void setUp() {
        // Ensure roles exist
        if (roleRepository.findByName(Role.ERole.ROLE_STUDENT).isEmpty()) {
            Role studentRole = new Role();
            studentRole.setName(Role.ERole.ROLE_STUDENT);
            roleRepository.save(studentRole);
        }
    }

    @Test
    void testSignup() throws Exception {
        SignupRequest signupRequest = new SignupRequest();
        signupRequest.setUsername("newuser");
        signupRequest.setEmail("newuser@example.com");
        signupRequest.setPassword("password123");
        signupRequest.setFirstName("New");
        signupRequest.setLastName("User");
        signupRequest.setRole(Set.of("student"));
        signupRequest.setStudentNumber("2024002");

        mockMvc.perform(post("/api/auth/signup")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(signupRequest)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.message").value("User registered successfully!"));
    }

    @Test
    void testSignupDuplicateUsername() throws Exception {
        // First signup
        SignupRequest signupRequest = new SignupRequest();
        signupRequest.setUsername("duplicateuser");
        signupRequest.setEmail("duplicate1@example.com");
        signupRequest.setPassword("password123");
        signupRequest.setFirstName("First");
        signupRequest.setLastName("User");
        signupRequest.setRole(Set.of("student"));

        mockMvc.perform(post("/api/auth/signup")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(signupRequest)))
                .andExpect(status().isOk());

        // Try to signup with same username
        signupRequest.setEmail("duplicate2@example.com");
        mockMvc.perform(post("/api/auth/signup")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(signupRequest)))
                .andExpect(status().isBadRequest());
    }

    @Test
    void testSignin() throws Exception {
        // First create a user
        SignupRequest signupRequest = new SignupRequest();
        signupRequest.setUsername("signinuser");
        signupRequest.setEmail("signin@example.com");
        signupRequest.setPassword("password123");
        signupRequest.setFirstName("Signin");
        signupRequest.setLastName("User");
        signupRequest.setRole(Set.of("student"));

        mockMvc.perform(post("/api/auth/signup")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(signupRequest)))
                .andExpect(status().isOk());

        // Now try to sign in
        LoginRequest loginRequest = new LoginRequest();
        loginRequest.setUsername("signinuser");
        loginRequest.setPassword("password123");

        mockMvc.perform(post("/api/auth/signin")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(loginRequest)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.token").exists())
                .andExpect(jsonPath("$.username").value("signinuser"));
    }

    @Test
    void testSigninInvalidCredentials() throws Exception {
        LoginRequest loginRequest = new LoginRequest();
        loginRequest.setUsername("nonexistent");
        loginRequest.setPassword("wrongpassword");

        mockMvc.perform(post("/api/auth/signin")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(loginRequest)))
                .andExpect(status().isUnauthorized());
    }
}

