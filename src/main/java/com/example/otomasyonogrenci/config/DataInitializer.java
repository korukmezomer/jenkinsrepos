package com.example.otomasyonogrenci.config;

import com.example.otomasyonogrenci.model.Role;
import com.example.otomasyonogrenci.repository.RoleRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

@Component
public class DataInitializer implements CommandLineRunner {
    @Autowired
    private RoleRepository roleRepository;

    @Override
    public void run(String... args) throws Exception {
        // Rolleri oluştur
        if (roleRepository.findByName(Role.ERole.ROLE_ADMIN).isEmpty()) {
            Role adminRole = new Role();
            adminRole.setName(Role.ERole.ROLE_ADMIN);
            roleRepository.save(adminRole);
        }

        if (roleRepository.findByName(Role.ERole.ROLE_TEACHER).isEmpty()) {
            Role teacherRole = new Role();
            teacherRole.setName(Role.ERole.ROLE_TEACHER);
            roleRepository.save(teacherRole);
        }

        if (roleRepository.findByName(Role.ERole.ROLE_STUDENT).isEmpty()) {
            Role studentRole = new Role();
            studentRole.setName(Role.ERole.ROLE_STUDENT);
            roleRepository.save(studentRole);
        }
    }
}

