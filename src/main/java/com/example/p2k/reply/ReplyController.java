package com.example.p2k.reply;

import com.example.p2k._core.security.CustomUserDetails;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

@Slf4j
@RequiredArgsConstructor
@RequestMapping("/posts/{postId}/replies")
@Controller
public class ReplyController {

    private final ReplyService replyService;

    //댓글 목록 조회
    @GetMapping
    public String findByPostId(@PathVariable Long postId, Model model){
        ReplyResponse.FindRepliesDTO commentsDTO = replyService.findByPostId(postId);
        log.info("댓글 목록 조회");
        model.addAttribute("commentsDTO", commentsDTO);
        log.info("댓글 목록 조회 완료");
        return "redirect:/courses";
    }

    //댓글 저장
    @PostMapping
    public String save(@ModelAttribute ReplyRequest.SaveDTO requestDTO, @PathVariable Long postId,
                       @AuthenticationPrincipal CustomUserDetails userDetails, HttpServletRequest request){
        log.info("댓글 저장");
        replyService.save(requestDTO, userDetails.getUser(), postId);
        log.info("댓글 저장 완료");
        return "redirect:" + request.getHeader("Referer");
    }

    //댓글 수정
    @PatchMapping("/{commentId}")
    public String update(@ModelAttribute ReplyRequest.UpdateDTO requestDTO, @PathVariable Long commentId){
        replyService.update(requestDTO, commentId);
        return "redirect:/courses";
    }

    //댓글 삭제
    @DeleteMapping("/{commentId}")
    public String delete(@PathVariable Long commentId){
        replyService.delete(commentId);
        return "redirect:/courses";
    }
}
