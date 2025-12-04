package com.example.otomasyonogrenci.repository;

import com.example.otomasyonogrenci.model.Role;
import com.example.otomasyonogrenci.model.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;
import java.util.List;

@Repository
public interface UserRepository extends JpaRepository<User, Long> {
    Optional<User> findByUsername(String username);
    Optional<User> findByEmail(String email);
    Boolean existsByUsername(String username);
    Boolean existsByEmail(String email);
    List<User> findByRoles_Name(Role.ERole roleName);
    
    // Öğrenci ve öğretmen sorguları için
    List<User> findByStudentNumberIsNotNull();
    List<User> findByDepartmentIsNotNull();
}

