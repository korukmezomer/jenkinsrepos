package com.example.otomasyonogrenci.service;

import com.example.otomasyonogrenci.model.Role;
import com.example.otomasyonogrenci.model.User;
import com.example.otomasyonogrenci.repository.RoleRepository;
import com.example.otomasyonogrenci.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@Service
public class UserService {
    @Autowired
    private UserRepository userRepository;

    @Autowired
    private RoleRepository roleRepository;

    public List<User> getAllUsers() {
        return userRepository.findAll();
    }

    public Optional<User> getUserById(Long id) {
        return userRepository.findById(id);
    }

    public Optional<User> getUserByUsername(String username) {
        return userRepository.findByUsername(username);
    }

    public List<User> getUsersByRole(Role.ERole role) {
        return userRepository.findByRoles_Name(role);
    }

    public List<User> getAllStudents() {
        return userRepository.findByStudentNumberIsNotNull();
    }

    public List<User> getAllTeachers() {
        return userRepository.findByDepartmentIsNotNull();
    }

    @Transactional
    public User updateUser(Long id, User userDetails) {
        User user = userRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("User not found"));

        user.setFirstName(userDetails.getFirstName());
        user.setLastName(userDetails.getLastName());
        user.setEmail(userDetails.getEmail());
        user.setPhoneNumber(userDetails.getPhoneNumber());
        user.setStudentNumber(userDetails.getStudentNumber());
        user.setDepartment(userDetails.getDepartment());

        return userRepository.save(user);
    }

    @Transactional
    public void deleteUser(Long id) {
        User user = userRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("User not found"));
        userRepository.delete(user);
    }
}

