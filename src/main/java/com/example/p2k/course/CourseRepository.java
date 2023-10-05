package com.example.p2k.course;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.repository.query.Param;

public interface CourseRepository extends JpaRepository<Course, Long> {

    Page<Course> findByNameContainingIgnoreCase(Pageable pageable, @Param("keyword") String keyword);
}
