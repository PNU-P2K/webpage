package com.example.p2k.reply;

import com.example.p2k.post.Post;
import com.example.p2k.post.PostRepository;
import com.example.p2k.user.User;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

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
    public void save(ReplyRequest.SaveDTO requestDTO, User user, Long postId){
        Post post = postRepository.findById(postId).get();
        Reply reply = Reply.builder().content(requestDTO.getContent()).user(user).post(post).build();
        replyRepository.save(reply);
    }

    //댓글 수정
    @Transactional
    public void update(ReplyRequest.UpdateDTO requestDTO, Long commentId){
        replyRepository.update(requestDTO.getContent(), commentId);
    }

    //댓글 삭제
    @Transactional
    public void delete(Long commentId){
        replyRepository.deleteById(commentId);
    }
}
