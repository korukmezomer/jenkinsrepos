package com.example.otomasyonogrenci.model;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Entity
@Table(name = "enrollments", 
    uniqueConstraints = {
        @UniqueConstraint(columnNames = {"student_id", "course_id"})
    })
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Enrollment {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "student_id", nullable = false)
    private User student; // Öğrenci

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "course_id", nullable = false)
    private Course course; // Ders

    @Column(nullable = false)
    private LocalDateTime enrollmentDate; // Kayıt tarihi

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 20)
    private EnrollmentStatus status; // Kayıt durumu

    @OneToOne(mappedBy = "enrollment", cascade = CascadeType.ALL, orphanRemoval = true)
    private Grade grade; // Not

    public enum EnrollmentStatus {
        ACTIVE,    // Aktif
        COMPLETED, // Tamamlandı
        DROPPED    // Bırakıldı
    }
}

