package com.example.p2k.course;

import com.example.p2k.course.courseuser.CourseUser;
import com.example.p2k.user.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface CourseRepository extends JpaRepository<Course, Long> {

    @Query("select cu.course from CourseUser cu where cu.user.id = :userId")
    List<Course> findByUserId(@Param("userId") Long userId);

    @Query("select cu.user from CourseUser cu where cu.course.id = :courseId")
    List<User> findAllUserByCourseId(@Param("courseId") Long courseId);
}
