package com.example.p2k.post;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

public interface PostRepository extends JpaRepository<Post, Long> {

    @Query("select p from Post p join fetch p.user where p.id = :postId")
    Optional<Post> findById(@Param("postId") Long postId);

    @Query("select p from Post p where p.course.id = :courseId and p.category = :category")
    List<Post> findPostByCategory(@Param("courseId") Long courseId, @Param("category") Category category);

    @Modifying
    @Query("update Post p SET p.title = :title, p.content = :content where p.id = :postId")
    void update(@Param("title") String title, @Param("content") String content, @Param("postId") Long postId);

    @Modifying
    @Query("delete from Post p where p.id = :postId")
    void deleteById(@Param("postId") Long postId);

    @Modifying
    @Query("delete from Post p where p.course.id = :courseId")
    void deleteAllByCourseId(@Param("courseId") Long courseId);
}
