package com.example.otomasyonogrenci.model;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.HashSet;
import java.util.Set;

@Entity
@Table(name = "courses")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Course {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, unique = true, length = 20)
    private String courseCode; // Ders kodu (örn: CS101)

    @Column(nullable = false, length = 100)
    private String courseName; // Ders adı

    @Column(length = 500)
    private String description; // Ders açıklaması

    @Column(nullable = false)
    private Integer credit; // Kredi

    @Column(nullable = false)
    private Integer quota; // Kontenjan

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "teacher_id", nullable = false)
    private User teacher; // Öğretmen

    @OneToMany(mappedBy = "course", cascade = CascadeType.ALL, orphanRemoval = true)
    private Set<Enrollment> enrollments = new HashSet<>();
}

