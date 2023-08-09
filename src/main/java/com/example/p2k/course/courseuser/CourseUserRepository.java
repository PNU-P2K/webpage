package com.example.p2k.course.courseuser;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface CourseUserRepository extends JpaRepository<CourseUser, Long> {

    @Modifying
    @Query("delete from CourseUser cu where cu.user.id = :userId and cu.course.id = :courseId")
    void deleteByUserIdAndCourseId(@Param("userId") Long userId, @Param("courseId") Long courseId);

    @Modifying
    @Query("delete from CourseUser cu where cu.course.id = :courseId")
    void deleteByCourseId(@Param("courseId") Long courseId);
}
