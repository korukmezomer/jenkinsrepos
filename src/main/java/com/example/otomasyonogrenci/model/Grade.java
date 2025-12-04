package com.example.otomasyonogrenci.model;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Entity
@Table(name = "grades")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Grade {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @OneToOne
    @JoinColumn(name = "enrollment_id", nullable = false, unique = true)
    private Enrollment enrollment; // Ders kaydı

    @Column
    private Double midtermGrade; // Vize notu

    @Column
    private Double finalGrade; // Final notu

    @Column
    private Double averageGrade; // Ortalama not

    @Column(length = 2)
    private String letterGrade; // Harf notu (AA, BA, BB, etc.)

    @Column
    private LocalDateTime gradeDate; // Not girilme tarihi

    public void calculateAverage() {
        if (midtermGrade != null && finalGrade != null) {
            this.averageGrade = (midtermGrade * 0.4) + (finalGrade * 0.6);
            calculateLetterGrade();
        }
    }

    private void calculateLetterGrade() {
        if (averageGrade == null) return;
        
        if (averageGrade >= 90) letterGrade = "AA";
        else if (averageGrade >= 85) letterGrade = "BA";
        else if (averageGrade >= 80) letterGrade = "BB";
        else if (averageGrade >= 75) letterGrade = "CB";
        else if (averageGrade >= 70) letterGrade = "CC";
        else if (averageGrade >= 65) letterGrade = "DC";
        else if (averageGrade >= 60) letterGrade = "DD";
        else if (averageGrade >= 50) letterGrade = "FD";
        else letterGrade = "FF";
    }
}

