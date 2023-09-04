package com.example.p2k.reply;

import com.example.p2k._core.exception.Exception404;
import com.example.p2k.post.Post;
import com.example.p2k.post.PostRepository;
import com.example.p2k.user.User;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@Slf4j
@Transactional(readOnly = true)
@RequiredArgsConstructor
@Service
public class ReplyService {

    private final ReplyRepository replyRepository;
    private final PostRepository postRepository;

    //댓글 목록 조회
    public ReplyResponse.FindRepliesDTO findByPostId(Long postId){
        List<Reply> replies = replyRepository.findByPostId(postId);
        return new ReplyResponse.FindRepliesDTO(replies);
    }

    //댓글 저장
    @Transactional
    public void saveComment(ReplyRequest.SaveCommentDTO requestDTO, User user, Long postId){
        Post post = postRepository.findById(postId).orElseThrow(
                () -> new Exception404("해당 게시글을 찾을 수 없습니다.")
        );

        Long maxRef = replyRepository.findMaxRef(postId);
        if(maxRef == null){
            maxRef = 0L;
        }else{
            maxRef += 1L;
        }

        Reply reply = Reply.builder()
                .content(requestDTO.getContent())
                .author(user.getName())
                .ref(maxRef)
                .refOrder(0L)
                .step(0L)
                .parentNum(0L)
                .answerNum(0L)
                .user(user)
                .post(post)
                .build();
        replyRepository.save(reply);
    }

    //답글 작성
    @Transactional
    public void saveReply(ReplyRequest.SaveReplyDTO requestDTO, User user, Long postId){
        Post post = postRepository.findById(postId).orElseThrow(
                () -> new Exception404("해당 게시글을 찾을 수 없습니다.")
        );

        Reply parentReply = replyRepository.findById(requestDTO.getParentId()).orElseThrow(
                () -> new Exception404("해당 댓글을 찾을 수 없습니다.")
        );

        Long refOrder = getRefOrder(parentReply);
        Reply reply = Reply.builder()
                .content(requestDTO.getContent())
                .author(user.getName())
                .ref(parentReply.getRef())
                .refOrder(refOrder)
                .step(parentReply.getStep() + 1L)
                .parentNum(parentReply.getId())
                .answerNum(0L)
                .user(user)
                .post(post)
                .build();

        replyRepository.save(reply);
        replyRepository.updateAnswerNum(parentReply.getId());
    }

    public Long getRefOrder(Reply parentReply){
        Long parentRef = parentReply.getRef();
        Long parentRefOrder = parentReply.getRefOrder();
        Long parentStep = parentReply.getStep();

        Long refOrder = replyRepository.findRefOrder(parentRef, parentStep, parentRefOrder);
        log.info("refOrder=" + refOrder);
        if(refOrder == null){
            return replyRepository.findMaxRefOrder(parentRef) + 1;
        }else{
            replyRepository.updateRefOrder(parentRef, refOrder);
            return refOrder;
        }
    }

    //댓글 삭제
    @Transactional
    public void delete(Long replyId){
        replyRepository.updateDeletedReply(replyId);
    }
}
