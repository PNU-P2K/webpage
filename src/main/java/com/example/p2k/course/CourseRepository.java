package com.example.p2k.course;

import com.example.p2k.user.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface CourseRepository extends JpaRepository<Course, Long> {

    @Query("select c from Course c where c.name like %:keyword%")
    List<Course> findByNameContaining(@Param("keyword") String keyword);
}
