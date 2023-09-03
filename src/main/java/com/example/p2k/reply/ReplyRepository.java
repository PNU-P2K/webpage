package com.example.p2k.reply;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface ReplyRepository extends JpaRepository<Reply, Long> {

    @Query("select r from Reply r where r.post.id = :postId order by r.ref, r.refOrder")
    List<Reply> findByPostId(@Param("postId") Long postId);

    @Query("select max(r.ref) from Reply r where r.post.id = :postId")
    Long findMaxRef(@Param("postId") Long postId);

    @Query("select min(r.refOrder) from Reply r where r.ref = :ref and r.step <= :step and r.refOrder > :refOrder")
    Long findRefOrder(@Param("ref") Long ref, @Param("step") Long step, @Param("refOrder") Long refOrder);

    @Query("select max(r.refOrder) from Reply r where r.ref = :ref")
    Long findMaxRefOrder(@Param("ref") Long ref);

    @Modifying(clearAutomatically = true)
    @Query("update Reply r SET r.content = :content where r.id = :commentId")
    void update(@Param("content") String content, @Param("commentId") Long commentId);

    @Modifying
    @Query("update Reply r SET r.refOrder = r.refOrder + 1 where r.ref = :ref and r.refOrder >= :refOrder")
    void updateRefOrder(@Param("ref") Long ref, @Param("refOrder") Long refOrder);

    @Modifying
    @Query("update Reply r SET r.answerNum = r.answerNum + 1 where r.id = :id")
    void updateAnswerNum(@Param("id") Long id);

    @Modifying
    @Query("update Reply r SET r.content = '삭제된 댓글입니다.', r.author = '비밀^8^' where r.id = :id")
    void updateDeletedReply(@Param("id") Long id);
}
