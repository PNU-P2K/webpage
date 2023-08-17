package com.example.p2k.post;

import com.example.p2k._core.security.CustomUserDetails;
import com.example.p2k.reply.ReplyRequest;
import com.example.p2k.reply.ReplyResponse;
import com.example.p2k.reply.ReplyService;
import com.example.p2k.course.CourseRequest;
import com.example.p2k.course.CourseResponse;
import com.example.p2k.course.CourseService;
import com.example.p2k.user.Role;
import com.example.p2k.user.User;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

@RequiredArgsConstructor
@RequestMapping("/courses/{courseId}")
@Controller
public class PostController {

    private final CourseService courseService;
    private final PostService postService;
    private final ReplyService replyService;

    //공지사항 게시판 페이지
    @GetMapping("/notice-board")
    public String getNoticeBoard(@PathVariable Long courseId, Model model, @AuthenticationPrincipal CustomUserDetails userDetails){
        CourseResponse.FindById courseDTO = courseService.findById(courseId);
        CourseResponse.FindPostsDTO postDTOs = postService.findPostsByCategory(courseId, Category.NOTICE);
        model.addAttribute("courseDTO", courseDTO);
        model.addAttribute("postDTOs", postDTOs);

        User user = userDetails.getUser();
        if(user.getRole() == Role.ROLE_STUDENT){
            return "course/student/noticeBoard";
        }else{
            return "course/instructor/noticeBoard";
        }
    }

    //질문 게시판 페이지
    @GetMapping("/question-board")
    public String getQuestionBoard(@PathVariable Long courseId, Model model, @AuthenticationPrincipal CustomUserDetails userDetails){
        CourseResponse.FindById courseDTO = courseService.findById(courseId);
        CourseResponse.FindPostsDTO postDTOs = postService.findPostsByCategory(courseId, Category.QUESTION);
        model.addAttribute("courseDTO", courseDTO);
        model.addAttribute("postDTOs", postDTOs);

        User user = userDetails.getUser();
        if(user.getRole() == Role.ROLE_STUDENT){
            return "course/student/questionBoard";
        }else{
            return "course/instructor/questionBoard";
        }
    }

    //자유 게시판 페이지
    @GetMapping("/free-board")
    public String getFreeBoard(@PathVariable Long courseId, Model model, @AuthenticationPrincipal CustomUserDetails userDetails){
        CourseResponse.FindById courseDTO = courseService.findById(courseId);
        CourseResponse.FindPostsDTO postDTOs = postService.findPostsByCategory(courseId, Category.FREE);
        model.addAttribute("courseDTO", courseDTO);
        model.addAttribute("postDTOs", postDTOs);

        User user = userDetails.getUser();
        if(user.getRole() == Role.ROLE_STUDENT){
            return "course/student/freeBoard";
        }else{
            return "course/instructor/freeBoard";
        }
    }

    //공지사항 글 상세 조회
    @GetMapping("/notice-board/{postId}")
    public String findNoticePost(@PathVariable Long courseId, @PathVariable Long postId, Model model,
                                 @AuthenticationPrincipal CustomUserDetails userDetails){
        CourseResponse.FindById courseDTO = courseService.findById(courseId);
        CourseResponse.FindPostByIdDTO postDTO = postService.findPostById(postId);
        ReplyResponse.FindRepliesDTO repliesDTO = replyService.findByPostId(postId);

        User user = userDetails.getUser();

        model.addAttribute("courseDTO", courseDTO);
        model.addAttribute("postDTO", postDTO);
        model.addAttribute("repliesDTO", repliesDTO);
        model.addAttribute("userId", user.getId());

        if(postDTO.getUserId().equals(user.getId())){
            return "course/instructor/noticePost";
        }else{
            return "course/student/noticePost";
        }
    }

    //공지사항 글 작성
    @PostMapping("/notice-board/save")
    public String saveNoticePost(@PathVariable Long courseId, @ModelAttribute CourseRequest.PostDTO requestDTO,
                                 @AuthenticationPrincipal CustomUserDetails userDetails){
        requestDTO.setCategory(Category.NOTICE);
        postService.savePost(requestDTO, userDetails.getUser(), courseId);
        return "redirect:/courses/{courseId}/notice-board";
    }

    //공지사항 글 작성 폼
    @GetMapping("/notice-board/save")
    public String getSaveNoticePost(@PathVariable Long courseId, Model model){
        CourseResponse.FindById courseDTO = courseService.findById(courseId);
        model.addAttribute("courseDTO", courseDTO);
        model.addAttribute("postDTO", new CourseRequest.PostDTO());
        return "course/instructor/saveNoticePost";
    }

    //공지사항 글 수정
    @PostMapping("/notice-board/{postId}/update")
    public String updateNoticePost(@PathVariable Long postId,
                                   @ModelAttribute CourseRequest.PostDTO requestDTO,
                                   @AuthenticationPrincipal CustomUserDetails userDetails){
        requestDTO.setCategory(Category.NOTICE);
        postService.updatePost(requestDTO, postId, userDetails.getUser());
        return "redirect:/courses/{courseId}/notice-board/{postId}";
    }

    //공지사항 글 수정 폼
    @GetMapping("/notice-board/{postId}/update")
    public String getUpdateNoticePost(@PathVariable Long courseId, @PathVariable Long postId, Model model){
        CourseResponse.FindById courseDTO = courseService.findById(courseId);
        CourseResponse.FindPostByIdDTO postDTO = postService.findPostById(postId);
        model.addAttribute("courseDTO", courseDTO);
        model.addAttribute("postDTO", postDTO);
        return "course/instructor/updateNoticePost";
    }

    //공지사항 글 삭제
    @GetMapping("/notice-board/{postId}/delete")
    public String deleteNoticePost(@PathVariable Long postId, @AuthenticationPrincipal CustomUserDetails userDetails){
        postService.deletePost(postId, userDetails.getUser());
        return "redirect:/courses/{courseId}/notice-board";
    }

    //질문 글 상세 조회
    @GetMapping("/question-board/{postId}")
    public String findQuestionPost(@PathVariable Long courseId, @PathVariable Long postId, Model model,
                                   @AuthenticationPrincipal CustomUserDetails userDetails){
        CourseResponse.FindById courseDTO = courseService.findById(courseId);
        CourseResponse.FindPostByIdDTO postDTO = postService.findPostById(postId);
        ReplyResponse.FindRepliesDTO repliesDTO = replyService.findByPostId(postId);

        User user = userDetails.getUser();

        model.addAttribute("courseDTO", courseDTO);
        model.addAttribute("postDTO", postDTO);
        model.addAttribute("repliesDTO", repliesDTO);
        model.addAttribute("userId", user.getId());

        if(postDTO.getUserId().equals(user.getId())){
            return "course/instructor/questionPost";

        }else{
            return "course/student/questionPost";
        }
    }

    //질문 글 작성
    @PostMapping("/question-board/save")
    public String saveQuestionPost(@PathVariable Long courseId, @ModelAttribute CourseRequest.PostDTO requestDTO,
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
        model.addAttribute("courseDTO", courseDTO);
        model.addAttribute("postDTO", new CourseRequest.PostDTO());

        User user = userDetails.getUser();
        if(user.getRole() == Role.ROLE_STUDENT){
            return "course/student/saveQuestionPost";
        }else{
            return "course/instructor/saveQuestionPost";
        }
    }

    //질문 글 수정
    @PostMapping("/question-board/{postId}/update")
    public String updateQuestionPost(@PathVariable Long postId,
                                     @ModelAttribute CourseRequest.PostDTO requestDTO,
                                     @AuthenticationPrincipal CustomUserDetails userDetails){
        requestDTO.setCategory(Category.QUESTION);
        postService.updatePost(requestDTO, postId, userDetails.getUser());
        return "redirect:/courses/{courseId}/question-board/{postId}";
    }

    //질문 글 수정 폼
    @GetMapping("/question-board/{postId}/update")
    public String getUpdateQuestionPost(@PathVariable Long courseId, @PathVariable Long postId, Model model,
                                        @AuthenticationPrincipal CustomUserDetails userDetails){
        CourseResponse.FindById courseDTO = courseService.findById(courseId);
        CourseResponse.FindPostByIdDTO postDTO = postService.findPostById(postId);
        model.addAttribute("courseDTO", courseDTO);
        model.addAttribute("postDTO", postDTO);

        User user = userDetails.getUser();
        if(user.getRole() == Role.ROLE_STUDENT){
            return "course/student/updateQuestionPost";
        }else{
            return "course/instructor/updateQuestionPost";
        }
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
                               @AuthenticationPrincipal CustomUserDetails userDetails){
        CourseResponse.FindById courseDTO = courseService.findById(courseId);
        CourseResponse.FindPostByIdDTO postDTO = postService.findPostById(postId);
        ReplyResponse.FindRepliesDTO repliesDTO = replyService.findByPostId(postId);

        User user = userDetails.getUser();

        model.addAttribute("courseDTO", courseDTO);
        model.addAttribute("postDTO", postDTO);
        model.addAttribute("repliesDTO", repliesDTO);
        model.addAttribute("userId", user.getId());

        if(postDTO.getUserId().equals(user.getId())){
            return "course/instructor/freePost";
        }else{
            return "course/student/freePost";
        }
    }

    //자유 글 작성
    @PostMapping("/free-board/save")
    public String saveFreePost(@PathVariable Long courseId, @ModelAttribute CourseRequest.PostDTO requestDTO,
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
        model.addAttribute("courseDTO", courseDTO);
        model.addAttribute("postDTO", new CourseRequest.PostDTO());

        User user = userDetails.getUser();
        if(user.getRole() == Role.ROLE_STUDENT){
            return "course/student/saveFreePost";
        }else{
            return "course/instructor/saveFreePost";
        }
    }

    //자유 글 수정
    @PostMapping("/free-board/{postId}/update")
    public String updateFreePost(@PathVariable Long postId,
                                 @ModelAttribute CourseRequest.PostDTO requestDTO,
                                 @AuthenticationPrincipal CustomUserDetails userDetails){
        requestDTO.setCategory(Category.FREE);
        postService.updatePost(requestDTO, postId, userDetails.getUser());
        return "redirect:/courses/{courseId}/free-board/{postId}";
    }

    //자유 글 수정 폼
    @GetMapping("/free-board/{postId}/update")
    public String getUpdateFreePost(@PathVariable Long courseId, @PathVariable Long postId, Model model,
                                    @AuthenticationPrincipal CustomUserDetails userDetails){
        CourseResponse.FindById courseDTO = courseService.findById(courseId);
        CourseResponse.FindPostByIdDTO postDTO = postService.findPostById(postId);
        model.addAttribute("courseDTO", courseDTO);
        model.addAttribute("postDTO", postDTO);

        User user = userDetails.getUser();
        if(user.getRole() == Role.ROLE_STUDENT){
            return "course/student/updateFreePost";
        }else{
            return "course/instructor/updateFreePost";
        }
    }

    //자유 글 삭제
    @GetMapping("/free-board/{postId}/delete")
    public String deleteFreePost(@PathVariable Long postId, @AuthenticationPrincipal CustomUserDetails userDetails){
        postService.deletePost(postId, userDetails.getUser());
        return "redirect:/courses/{courseId}/free-board";
    }
}
