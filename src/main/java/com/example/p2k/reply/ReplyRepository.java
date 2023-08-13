package com.example.p2k.reply;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface ReplyRepository extends JpaRepository<Reply, Long> {

    @Query("select c from Reply c where c.post.id = :postId")
    List<Reply> findByPostId(@Param("postId") Long postId);

    @Modifying(clearAutomatically = true)
    @Query("update Reply c SET c.content = :content where c.id = :commentId")
    void update(@Param("content") String content, @Param("commentId") Long commentId);
}
