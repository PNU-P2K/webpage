package com.example.p2k.post;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.Optional;

public interface PostRepository extends JpaRepository<Post, Long> {

    @Query("select p from Post p join fetch p.user where p.id = :postId")
    Optional<Post> findById(@Param("postId") Long postId);

    @Query("select p from Post p where p.course.id = :courseId and p.category = :category and (p.scope = :scope or p.user.id = :userId)")
    Page<Post> findPostByCategory(Pageable pageable, @Param("courseId") Long courseId, @Param("category") Category category, @Param("scope") Scope scope, @Param("userId") Long userId);

    @Modifying
    @Query("update Post p SET p.title = :title, p.content = :content, p.scope = :scope where p.id = :postId")
    void update(@Param("title") String title, @Param("content") String content, @Param("scope") Scope scope, @Param("postId") Long postId);

    @Modifying
    @Query("delete from Post p where p.id = :postId")
    void deleteById(@Param("postId") Long postId);

    @Modifying
    @Query("delete from Post p where p.course.id = :courseId")
    void deleteAllByCourseId(@Param("courseId") Long courseId);
}
