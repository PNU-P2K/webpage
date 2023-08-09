package com.example.p2k.course;

import com.example.p2k._core.security.CustomUserDetails;
import com.example.p2k.course.post.Category;
import com.example.p2k.user.Role;
import com.example.p2k.user.User;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Slf4j
@RequiredArgsConstructor
@RequestMapping("/courses")
@Controller
public class CourseController {

    private final CourseService courseService;

    //나의 강좌 조회
    @GetMapping
    public String findCourses(Model model, @AuthenticationPrincipal CustomUserDetails userDetails){
        User user = userDetails.getUser();
        CourseResponse.FindCoursesDTO courseDTOs = courseService.findCourses(user.getId());
        model.addAttribute("courseDTOs", courseDTOs);

        if(user.getRole() == Role.ROLE_STUDENT){
            return "course/student/course";
        }else{
            return "course/instructor/course";
        }
    }

    //강좌 신청 페이지
    @GetMapping("/apply")
    public String applyForm(Model model){
        CourseResponse.FindCoursesDTO courseDTOs = courseService.findAll();
        model.addAttribute("courseDTOs", courseDTOs);
        return "course/student/apply";
    }

    //강좌 신청
    @PostMapping("/apply/{courseId}")
    public String apply(@PathVariable Long courseId, @AuthenticationPrincipal CustomUserDetails userDetails){
        courseService.apply(courseId, userDetails.getUser());
        return "redirect:/courses";
    }

    //나의 가상 환경 조회
    @GetMapping("/{courseId}/my-vm")
    public String findMyVm(@PathVariable Long courseId, Model model, @AuthenticationPrincipal CustomUserDetails userDetails){
        List<CourseResponse.FindMyVmDTO> myVms = courseService.findMyVm(courseId);
        CourseResponse.FindById courseDTO = courseService.findById(courseId);
        model.addAttribute("myVms", myVms);
        model.addAttribute("courseDTO", courseDTO);

        User user = userDetails.getUser();
        if(user.getRole() == Role.ROLE_STUDENT){
            return "course/student/myVm";
        }else{
            return "course/instructor/myVm";
        }
    }

    //교육자의 가상 환경 조회
    @GetMapping("/{courseId}/instructor-vm")
    public String findInstructorVm(@PathVariable Long courseId, Model model){
        List<CourseResponse.FindInstructorVmDTO> instructorVms = courseService.findInstructorVm(courseId);
        CourseResponse.FindById courseDTO = courseService.findById(courseId);
        model.addAttribute("instructorVms", instructorVms);
        model.addAttribute("courseDTO", courseDTO);
        return "course/student/instructorVm";
    }

    //강좌 취소
    @GetMapping("/{courseId}/cancel")
    public String cancel(@PathVariable Long courseId, @AuthenticationPrincipal CustomUserDetails userDetails){
        courseService.cancel(courseId, userDetails.getUser());
        return "redirect:/courses";
    }

    //강좌 생성
    @PostMapping("/create")
    public String create(@ModelAttribute CourseRequest.SaveDTO requestDTO, @AuthenticationPrincipal CustomUserDetails userDetails){
        courseService.create(requestDTO, userDetails.getUser());
        return "redirect:/courses";
    }

    //강좌 생성 폼
    @GetMapping("/create")
    public String createForm(Model model){
        model.addAttribute("saveDTO", new CourseRequest.SaveDTO());
        return "course/instructor/create";
    }

    //수강생 관리 페이지
    @GetMapping("/{courseId}/students")
    public String findStudents(@PathVariable Long courseId, Model model){
        CourseResponse.FindStudentsDTO studentDTOs = courseService.findStudents(courseId);
        CourseResponse.FindById courseDTO = courseService.findById(courseId);
        model.addAttribute("studentDTOs", studentDTOs);
        model.addAttribute("courseDTO", courseDTO);
        return "course/instructor/students";
    }

    //설정 및 관리 페이지
    @GetMapping("/{courseId}/setting")
    public String setting(@PathVariable Long courseId, Model model){
        CourseResponse.FindById courseDTO = courseService.findById(courseId);
        CourseResponse.FindStudentsDTO studentDTOs = courseService.findStudents(courseId);
        model.addAttribute("courseDTO", courseDTO);
        model.addAttribute("studentDTOs", studentDTOs);
        return "course/instructor/setting";
    }

    //강좌 삭제
    @PostMapping("/{courseId}/delete")
    public String delete(@PathVariable Long courseId){
        courseService.delete(courseId);
        return "redirect:/courses";
    }

    //공지사항 게시판 페이지
    @GetMapping("/{courseId}/notice-board")
    public String getNoticeBoard(@PathVariable Long courseId, Model model, @AuthenticationPrincipal CustomUserDetails userDetails){
        CourseResponse.FindById courseDTO = courseService.findById(courseId);
        CourseResponse.FindPostsDTO postDTOs = courseService.findPostsByCategory(courseId, Category.NOTICE);
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
    @GetMapping("/{courseId}/question-board")
    public String getQuestionBoard(@PathVariable Long courseId, Model model, @AuthenticationPrincipal CustomUserDetails userDetails){
        CourseResponse.FindById courseDTO = courseService.findById(courseId);
        CourseResponse.FindPostsDTO postDTOs = courseService.findPostsByCategory(courseId, Category.QUESTION);
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
    @GetMapping("/{courseId}/free-board")
    public String getFreeBoard(@PathVariable Long courseId, Model model, @AuthenticationPrincipal CustomUserDetails userDetails){
        CourseResponse.FindById courseDTO = courseService.findById(courseId);
        CourseResponse.FindPostsDTO postDTOs = courseService.findPostsByCategory(courseId, Category.FREE);
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
    @GetMapping("/{courseId}/notice-board/{postId}")
    public String findNoticePost(@PathVariable Long courseId, @PathVariable Long postId, Model model,
                                 @AuthenticationPrincipal CustomUserDetails userDetails){
        CourseResponse.FindById courseDTO = courseService.findById(courseId);
        CourseResponse.FindPostByIdDTO postDTO = courseService.findPostById(postId);
        model.addAttribute("courseDTO", courseDTO);
        model.addAttribute("postDTO", postDTO);

        User user = userDetails.getUser();
        if(postDTO.getUserId().equals(user.getId())){
            return "course/instructor/noticePost";
        }else{
            return "course/student/noticePost";
        }
    }

    //공지사항 글 작성
    @PostMapping("/{courseId}/notice-board/save")
    public String saveNoticePost(@PathVariable Long courseId, @ModelAttribute CourseRequest.PostDTO requestDTO,
                                 @AuthenticationPrincipal CustomUserDetails userDetails){
        requestDTO.setCategory(Category.NOTICE);
        courseService.savePost(requestDTO, userDetails.getUser(), courseId);
        return "redirect:/courses/{courseId}/notice-board";
    }

    //공지사항 글 작성 폼
    @GetMapping("/{courseId}/notice-board/save")
    public String getSaveNoticePost(@PathVariable Long courseId, Model model){
        CourseResponse.FindById courseDTO = courseService.findById(courseId);
        model.addAttribute("courseDTO", courseDTO);
        model.addAttribute("postDTO", new CourseRequest.PostDTO());
        return "course/instructor/saveNoticePost";
    }

    //공지사항 글 수정
    @PostMapping("/{courseId}/notice-board/{postId}/update")
    public String updateNoticePost(@PathVariable Long postId,
                                   @ModelAttribute CourseRequest.PostDTO requestDTO,
                                   @AuthenticationPrincipal CustomUserDetails userDetails){
        requestDTO.setCategory(Category.NOTICE);
        courseService.updatePost(requestDTO, postId, userDetails.getUser());
        return "redirect:/courses/{courseId}/notice-board/{postId}";
    }

    //공지사항 글 수정 폼
    @GetMapping("/{courseId}/notice-board/{postId}/update")
    public String getUpdateNoticePost(@PathVariable Long courseId, @PathVariable Long postId, Model model){
        CourseResponse.FindById courseDTO = courseService.findById(courseId);
        CourseResponse.FindPostByIdDTO postDTO = courseService.findPostById(postId);
        model.addAttribute("courseDTO", courseDTO);
        model.addAttribute("postDTO", postDTO);
        return "course/instructor/updateNoticePost";
    }

    //공지사항 글 삭제
    @GetMapping("/{courseId}/notice-board/{postId}/delete")
    public String deleteNoticePost(@PathVariable Long postId, @AuthenticationPrincipal CustomUserDetails userDetails){
        courseService.deletePost(postId, userDetails.getUser());
        return "redirect:/courses/{courseId}/notice-board";
    }

    //질문 글 상세 조회
    @GetMapping("/{courseId}/question-board/{postId}")
    public String findQuestionPost(@PathVariable Long courseId, @PathVariable Long postId, Model model,
                                   @AuthenticationPrincipal CustomUserDetails userDetails){
        CourseResponse.FindById courseDTO = courseService.findById(courseId);
        CourseResponse.FindPostByIdDTO postDTO = courseService.findPostById(postId);
        model.addAttribute("courseDTO", courseDTO);
        model.addAttribute("postDTO", postDTO);

        User user = userDetails.getUser();
        if(postDTO.getUserId().equals(user.getId())){
            return "course/instructor/questionPost";

        }else{
            return "course/student/questionPost";
        }
    }

    //질문 글 작성
    @PostMapping("/{courseId}/question-board/save")
    public String saveQuestionPost(@PathVariable Long courseId, @ModelAttribute CourseRequest.PostDTO requestDTO,
                                 @AuthenticationPrincipal CustomUserDetails userDetails){
        requestDTO.setCategory(Category.QUESTION);
        courseService.savePost(requestDTO, userDetails.getUser(), courseId);
        return "redirect:/courses/{courseId}/question-board";
    }

    //질문 글 작성 폼
    @GetMapping("/{courseId}/question-board/save")
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
    @PostMapping("/{courseId}/question-board/{postId}/update")
    public String updateQuestionPost(@PathVariable Long postId,
                                     @ModelAttribute CourseRequest.PostDTO requestDTO,
                                     @AuthenticationPrincipal CustomUserDetails userDetails){
        requestDTO.setCategory(Category.QUESTION);
        courseService.updatePost(requestDTO, postId, userDetails.getUser());
        return "redirect:/courses/{courseId}/question-board/{postId}";
    }

    //질문 글 수정 폼
    @GetMapping("/{courseId}/question-board/{postId}/update")
    public String getUpdateQuestionPost(@PathVariable Long courseId, @PathVariable Long postId, Model model,
                                        @AuthenticationPrincipal CustomUserDetails userDetails){
        CourseResponse.FindById courseDTO = courseService.findById(courseId);
        CourseResponse.FindPostByIdDTO postDTO = courseService.findPostById(postId);
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
    @GetMapping("/{courseId}/question-board/{postId}/delete")
    public String deleteQuestionPost(@PathVariable Long postId, @AuthenticationPrincipal CustomUserDetails userDetails){
        courseService.deletePost(postId, userDetails.getUser());
        return "redirect:/courses/{courseId}/question-board";
    }

    //자유 글 상세 조회
    @GetMapping("/{courseId}/free-board/{postId}")
    public String findFreePost(@PathVariable Long courseId, @PathVariable Long postId, Model model,
                               @AuthenticationPrincipal CustomUserDetails userDetails){
        CourseResponse.FindById courseDTO = courseService.findById(courseId);
        CourseResponse.FindPostByIdDTO postDTO = courseService.findPostById(postId);
        model.addAttribute("courseDTO", courseDTO);
        model.addAttribute("postDTO", postDTO);

        User user = userDetails.getUser();
        if(postDTO.getUserId().equals(user.getId())){
            return "course/instructor/freePost";
        }else{
            return "course/student/freePost";
        }
    }

    //자유 글 작성
    @PostMapping("/{courseId}/free-board/save")
    public String saveFreePost(@PathVariable Long courseId, @ModelAttribute CourseRequest.PostDTO requestDTO,
                                   @AuthenticationPrincipal CustomUserDetails userDetails){
        requestDTO.setCategory(Category.FREE);
        courseService.savePost(requestDTO, userDetails.getUser(), courseId);
        return "redirect:/courses/{courseId}/free-board";
    }

    //자유 글 작성 폼
    @GetMapping("/{courseId}/free-board/save")
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
    @PostMapping("/{courseId}/free-board/{postId}/update")
    public String updateFreePost(@PathVariable Long postId,
                                 @ModelAttribute CourseRequest.PostDTO requestDTO,
                                 @AuthenticationPrincipal CustomUserDetails userDetails){
        requestDTO.setCategory(Category.FREE);
        courseService.updatePost(requestDTO, postId, userDetails.getUser());
        return "redirect:/courses/{courseId}/free-board/{postId}";
    }

    //자유 글 수정 폼
    @GetMapping("/{courseId}/free-board/{postId}/update")
    public String getUpdateFreePost(@PathVariable Long courseId, @PathVariable Long postId, Model model,
                                    @AuthenticationPrincipal CustomUserDetails userDetails){
        CourseResponse.FindById courseDTO = courseService.findById(courseId);
        CourseResponse.FindPostByIdDTO postDTO = courseService.findPostById(postId);
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
    @GetMapping("/{courseId}/free-board/{postId}/delete")
    public String deleteFreePost(@PathVariable Long postId, @AuthenticationPrincipal CustomUserDetails userDetails){
        courseService.deletePost(postId, userDetails.getUser());
        return "redirect:/courses/{courseId}/free-board";
    }
}
