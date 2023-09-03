package com.example.p2k.reply;

import com.example.p2k._core.security.CustomUserDetails;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

@Slf4j
@RequiredArgsConstructor
@RequestMapping("/posts/{postId}/replies")
@Controller
public class ReplyController {

    private final ReplyService replyService;

    //댓글 저장
    @PostMapping
    public String saveComment(@ModelAttribute ReplyRequest.SaveCommentDTO requestDTO, @PathVariable Long postId,
                              @AuthenticationPrincipal CustomUserDetails userDetails, HttpServletRequest request){
        replyService.saveComment(requestDTO, userDetails.getUser(), postId);
        return "redirect:" + request.getHeader("Referer");
    }

    //답글 저장
    @PostMapping("/{replyId}")
    public String saveReply(@ModelAttribute ReplyRequest.SaveReplyDTO requestDTO, @PathVariable Long postId,
                            @AuthenticationPrincipal CustomUserDetails userDetails, HttpServletRequest request){
        replyService.saveReply(requestDTO, userDetails.getUser(), postId);
        return "redirect:" + request.getHeader("Referer");
    }

    //댓글 삭제
    @GetMapping("/{replyId}/delete")
    public String delete(@PathVariable Long replyId, HttpServletRequest request){
        replyService.delete(replyId);
        return "redirect:" + request.getHeader("Referer");
    }
}
