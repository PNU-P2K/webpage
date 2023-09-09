package com.example.p2k.course;

import com.example.p2k.user.User;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface CourseRepository extends JpaRepository<Course, Long> {

    @Query("select c from Course c where LOWER(c.name) like LOWER(CONCAT('%', :keyword, '%'))")
    Page<Course> findByNameContaining(Pageable pageable, @Param("keyword") String keyword);
}
