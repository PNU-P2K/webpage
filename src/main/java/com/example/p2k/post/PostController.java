package com.example.p2k.post;

import com.example.p2k._core.security.CustomUserDetails;
import com.example.p2k.reply.ReplyResponse;
import com.example.p2k.reply.ReplyService;
import com.example.p2k.course.CourseResponse;
import com.example.p2k.course.CourseService;
import com.example.p2k.user.User;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

@Slf4j
@RequiredArgsConstructor
@RequestMapping("/courses/{courseId}")
@Controller
public class PostController {

    private final CourseService courseService;
    private final PostService postService;
    private final ReplyService replyService;

    //              교육자        학생
    // 공지사항   :   작성 가능     조회만 가능
    // 질문, 자유 :   작성 가능     작성 가능
    //

    //공지사항 게시판 페이지
    @GetMapping("/notice-board")
    public String getNoticeBoard(@PathVariable Long courseId, Model model,
                                 @RequestParam(value = "page", defaultValue = "0") int page,
                                 @AuthenticationPrincipal CustomUserDetails userDetails){
        User user = userDetails.getUser();
        CourseResponse.FindById courseDTO = courseService.findById(courseId);
        PostResponse.FindPostsDTO postDTOs = postService.findPostsByCategory(courseId, page, Category.NOTICE, user);
        model.addAttribute("user", user);
        model.addAttribute("courseDTO", courseDTO);
        model.addAttribute("postDTOs", postDTOs);
        return "course/noticeBoard";
    }

    //질문 게시판 페이지
    @GetMapping("/question-board")
    public String getQuestionBoard(@PathVariable Long courseId, Model model,
                                   @RequestParam(value = "page", defaultValue = "0") int page,
                                   @AuthenticationPrincipal CustomUserDetails userDetails){
        User user = userDetails.getUser();
        CourseResponse.FindById courseDTO = courseService.findById(courseId);
        PostResponse.FindPostsDTO postDTOs = postService.findPostsByCategory(courseId, page, Category.QUESTION, user);
        model.addAttribute("user", user);
        model.addAttribute("courseDTO", courseDTO);
        model.addAttribute("postDTOs", postDTOs);
        return "course/questionBoard";
    }

    //자유 게시판 페이지
    @GetMapping("/free-board")
    public String getFreeBoard(@PathVariable Long courseId, Model model,
                               @RequestParam(value = "page", defaultValue = "0") int page,
                               @AuthenticationPrincipal CustomUserDetails userDetails){
        User user = userDetails.getUser();
        CourseResponse.FindById courseDTO = courseService.findById(courseId);
        PostResponse.FindPostsDTO postDTOs = postService.findPostsByCategory(courseId, page, Category.FREE, user);
        model.addAttribute("user", user);
        model.addAttribute("courseDTO", courseDTO);
        model.addAttribute("postDTOs", postDTOs);
        return "course/freeBoard";
    }

    //공지사항 글 상세 조회
    @GetMapping("/notice-board/{postId}")
    public String findNoticePost(@PathVariable Long courseId, @PathVariable Long postId, Model model,
                                 @RequestParam(value = "page", defaultValue = "0") int page,
                                 @AuthenticationPrincipal CustomUserDetails userDetails){
        User user = userDetails.getUser();
        CourseResponse.FindById courseDTO = courseService.findById(courseId);
        PostResponse.FindPostByIdDTO postDTO = postService.findPostById(postId);
        ReplyResponse.FindRepliesDTO repliesDTO = replyService.findByPostId(postId, page);
        model.addAttribute("courseDTO", courseDTO);
        model.addAttribute("postDTO", postDTO);
        model.addAttribute("repliesDTO", repliesDTO);
        model.addAttribute("user", user);
        return "course/noticePost";
    }

    //공지사항 글 작성
    @PostMapping("/notice-board/save")
    public String saveNoticePost(@PathVariable Long courseId, @ModelAttribute PostRequest.SaveDTO requestDTO,
                                 @AuthenticationPrincipal CustomUserDetails userDetails){
        requestDTO.setCategory(Category.NOTICE);
        requestDTO.setOpen(true);
        postService.savePost(requestDTO, userDetails.getUser(), courseId);
        return "redirect:/courses/{courseId}/notice-board";
    }

    //공지사항 글 작성 폼
    @GetMapping("/notice-board/save")
    public String getSaveNoticePost(@PathVariable Long courseId, Model model, @AuthenticationPrincipal CustomUserDetails userDetails){
        CourseResponse.FindById courseDTO = courseService.findById(courseId);
        User user = userDetails.getUser();
        model.addAttribute("user", user);
        model.addAttribute("courseDTO", courseDTO);
        model.addAttribute("saveDTO", new PostRequest.SaveDTO());
        return "course/saveNoticePost";
    }

    //공지사항 글 수정
    @PostMapping("/notice-board/{postId}/update")
    public String updateNoticePost(@PathVariable Long postId,
                                   @ModelAttribute PostRequest.UpdateDTO requestDTO,
                                   @AuthenticationPrincipal CustomUserDetails userDetails){
        postService.updatePost(requestDTO, postId, userDetails.getUser());
        return "redirect:/courses/{courseId}/notice-board/{postId}";
    }

    //공지사항 글 수정 폼
    @GetMapping("/notice-board/{postId}/update")
    public String getUpdateNoticePost(@PathVariable Long courseId, @PathVariable Long postId, Model model,
                                      @AuthenticationPrincipal CustomUserDetails userDetails){
        CourseResponse.FindById courseDTO = courseService.findById(courseId);
        PostResponse.FindPostByIdDTO postDTO = postService.findPostById(postId);
        User user = userDetails.getUser();
        model.addAttribute("user", user);
        model.addAttribute("courseDTO", courseDTO);
        model.addAttribute("updateDTO", postDTO);
        return "course/updateNoticePost";
    }

    //공지사항 글 삭제
    @GetMapping("/notice-board/{postId}/delete")
    public String deleteNoticePost(@PathVariable Long postId, Model model,
                                   @AuthenticationPrincipal CustomUserDetails userDetails){
        User user = userDetails.getUser();
        model.addAttribute("user", user);
        postService.deletePost(postId, userDetails.getUser());
        return "redirect:/courses/{courseId}/notice-board";
    }

    //질문 글 상세 조회
    @GetMapping("/question-board/{postId}")
    public String findQuestionPost(@PathVariable Long courseId, @PathVariable Long postId, Model model,
                                   @RequestParam(value = "page", defaultValue = "0") int page,
                                   @AuthenticationPrincipal CustomUserDetails userDetails){
        User user = userDetails.getUser();
        CourseResponse.FindById courseDTO = courseService.findById(courseId);
        PostResponse.FindPostByIdDTO postDTO = postService.findPostById(postId);
        ReplyResponse.FindRepliesDTO repliesDTO = replyService.findByPostId(postId, page);
        model.addAttribute("courseDTO", courseDTO);
        model.addAttribute("postDTO", postDTO);
        model.addAttribute("repliesDTO", repliesDTO);
        model.addAttribute("user", user);

        return "course/questionPost";
    }

    //질문 글 작성
    @PostMapping("/question-board/save")
    public String saveQuestionPost(@PathVariable Long courseId, @ModelAttribute PostRequest.SaveDTO requestDTO,
                                   @AuthenticationPrincipal CustomUserDetails userDetails){
        requestDTO.setCategory(Category.QUESTION);
        postService.savePost(requestDTO, userDetails.getUser(), courseId);
        return "redirect:/courses/{courseId}/question-board";
    }

    //질문 글 작성 폼
    @GetMapping("/question-board/save")
    public String getSaveQuestionPost(@PathVariable Long courseId, Model model,
                                      @AuthenticationPrincipal CustomUserDetails userDetails){
        CourseResponse.FindById courseDTO = courseService.findById(courseId);
        User user = userDetails.getUser();
        model.addAttribute("user", user);
        model.addAttribute("courseDTO", courseDTO);
        model.addAttribute("saveDTO", new PostRequest.SaveDTO());
        return "course/saveQuestionPost";
    }

    //질문 글 수정
    @PostMapping("/question-board/{postId}/update")
    public String updateQuestionPost(@PathVariable Long postId,
                                     @ModelAttribute PostRequest.UpdateDTO requestDTO,
                                     @AuthenticationPrincipal CustomUserDetails userDetails){
        postService.updatePost(requestDTO, postId, userDetails.getUser());
        return "redirect:/courses/{courseId}/question-board/{postId}";
    }

    //질문 글 수정 폼
    @GetMapping("/question-board/{postId}/update")
    public String getUpdateQuestionPost(@PathVariable Long courseId, @PathVariable Long postId, Model model,
                                        @AuthenticationPrincipal CustomUserDetails userDetails){
        User user = userDetails.getUser();
        CourseResponse.FindById courseDTO = courseService.findById(courseId);
        PostResponse.FindPostByIdDTO postDTO = postService.findPostById(postId);
        model.addAttribute("user", user);
        model.addAttribute("courseDTO", courseDTO);
        model.addAttribute("updateDTO", postDTO);
        return "course/updateQuestionPost";
    }

    //질문 글 삭제
    @GetMapping("/question-board/{postId}/delete")
    public String deleteQuestionPost(@PathVariable Long postId, @AuthenticationPrincipal CustomUserDetails userDetails){
        postService.deletePost(postId, userDetails.getUser());
        return "redirect:/courses/{courseId}/question-board";
    }

    //자유 글 상세 조회
    @GetMapping("/free-board/{postId}")
    public String findFreePost(@PathVariable Long courseId, @PathVariable Long postId, Model model,
                               @RequestParam(value = "page", defaultValue = "0") int page,
                               @AuthenticationPrincipal CustomUserDetails userDetails){
        User user = userDetails.getUser();
        CourseResponse.FindById courseDTO = courseService.findById(courseId);
        PostResponse.FindPostByIdDTO postDTO = postService.findPostById(postId);
        ReplyResponse.FindRepliesDTO repliesDTO = replyService.findByPostId(postId, page);
        model.addAttribute("courseDTO", courseDTO);
        model.addAttribute("postDTO", postDTO);
        model.addAttribute("repliesDTO", repliesDTO);
        model.addAttribute("user", user);
        return "course/freePost";
    }

    //자유 글 작성
    @PostMapping("/free-board/save")
    public String saveFreePost(@PathVariable Long courseId, @ModelAttribute PostRequest.SaveDTO requestDTO,
                               @AuthenticationPrincipal CustomUserDetails userDetails){
        requestDTO.setCategory(Category.FREE);
        postService.savePost(requestDTO, userDetails.getUser(), courseId);
        return "redirect:/courses/{courseId}/free-board";
    }

    //자유 글 작성 폼
    @GetMapping("/free-board/save")
    public String getSaveFreePost(@PathVariable Long courseId, Model model,
                                  @AuthenticationPrincipal CustomUserDetails userDetails){
        CourseResponse.FindById courseDTO = courseService.findById(courseId);
        User user = userDetails.getUser();
        model.addAttribute("user", user);
        model.addAttribute("courseDTO", courseDTO);
        model.addAttribute("saveDTO", new PostRequest.SaveDTO());
        return "course/saveFreePost";
    }

    //자유 글 수정
    @PostMapping("/free-board/{postId}/update")
    public String updateFreePost(@PathVariable Long postId,
                                 @ModelAttribute PostRequest.UpdateDTO requestDTO,
                                 @AuthenticationPrincipal CustomUserDetails userDetails){
        postService.updatePost(requestDTO, postId, userDetails.getUser());
        return "redirect:/courses/{courseId}/free-board/{postId}";
    }

    //자유 글 수정 폼
    @GetMapping("/free-board/{postId}/update")
    public String getUpdateFreePost(@PathVariable Long courseId, @PathVariable Long postId, Model model,
                                    @AuthenticationPrincipal CustomUserDetails userDetails){
        CourseResponse.FindById courseDTO = courseService.findById(courseId);
        PostResponse.FindPostByIdDTO postDTO = postService.findPostById(postId);
        User user = userDetails.getUser();
        model.addAttribute("user", user);
        model.addAttribute("courseDTO", courseDTO);
        model.addAttribute("updateDTO", postDTO);
        return "course/updateFreePost";
    }

    //자유 글 삭제
    @GetMapping("/free-board/{postId}/delete")
    public String deleteFreePost(@PathVariable Long postId, @AuthenticationPrincipal CustomUserDetails userDetails){
        postService.deletePost(postId, userDetails.getUser());
        return "redirect:/courses/{courseId}/free-board";
    }
}
