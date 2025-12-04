package com.example.otomasyonogrenci.repository;

import com.example.otomasyonogrenci.model.Course;
import com.example.otomasyonogrenci.model.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface CourseRepository extends JpaRepository<Course, Long> {
    Optional<Course> findByCourseCode(String courseCode);
    Boolean existsByCourseCode(String courseCode);
    List<Course> findByTeacher(User teacher);
    List<Course> findByTeacherId(Long teacherId);
}

