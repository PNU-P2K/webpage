package com.example.p2k.course;

import com.example.p2k._core.security.CustomUserDetails;
import com.example.p2k.user.Role;
import com.example.p2k.user.User;
import com.example.p2k.vm.VmResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

@Slf4j
@RequiredArgsConstructor
@RequestMapping("/courses")
@Controller
public class CourseController {

    private final CourseService courseService;

    //나의 강좌 조회
    @GetMapping
    public String findCourses(Model model, @RequestParam(value = "page", defaultValue = "0") int page,
                              @AuthenticationPrincipal CustomUserDetails userDetails){
        User user = userDetails.getUser();
        CourseResponse.FindCoursesDTO courseDTOs = courseService.findCourses(user.getId(), page);
        model.addAttribute("courseDTOs", courseDTOs);
        model.addAttribute("user", user);
        return "course/course";
    }

    //강좌 신청 페이지
    @GetMapping("/apply")
    public String applyForm(Model model, @RequestParam(value = "page", defaultValue = "0") int page,
                            @AuthenticationPrincipal CustomUserDetails userDetails){
        CourseResponse.FindCoursesDTO courseDTOs = courseService.findAll(page);
        User user = userDetails.getUser();
        model.addAttribute("courseDTOs", courseDTOs);
        model.addAttribute("user", user);
        return "course/apply";
    }

    //강좌 신청
    @PostMapping("/apply/{courseId}")
    public String apply(@PathVariable Long courseId, @AuthenticationPrincipal CustomUserDetails userDetails){
        courseService.apply(courseId, userDetails.getUser());
        return "redirect:/courses";
    }

    //강좌 검색
    @PostMapping("/apply/search")
    public String search(@RequestParam String keyword, Model model, @RequestParam(value = "page", defaultValue = "0") int page,
                         @AuthenticationPrincipal CustomUserDetails userDetails){
        CourseResponse.FindCoursesDTO courseDTOs = courseService.findBySearch(keyword, page);
        User user = userDetails.getUser();

        model.addAttribute("courseDTOs", courseDTOs);
        model.addAttribute("user", user);

        return "course/apply";
    }

    //나의 가상 환경 조회
    @GetMapping("/{courseId}/my-vm")
    public String findMyVm(@PathVariable Long courseId, Model model, @AuthenticationPrincipal CustomUserDetails userDetails){
        User user = userDetails.getUser();
        VmResponse.FindAllDTO myVms = courseService.findMyVm(user, courseId);
        CourseResponse.FindById courseDTO = courseService.findById(courseId);

        model.addAttribute("user", user);
        model.addAttribute("myVms", myVms);
        model.addAttribute("courseDTO", courseDTO);

        return "course/myVm";
    }

    //교육자의 가상 환경 조회
    @GetMapping("/{courseId}/instructor-vm")
    public String findInstructorVm(@PathVariable Long courseId, Model model, @AuthenticationPrincipal CustomUserDetails userDetails){
        VmResponse.FindAllDTO instructorVms = courseService.findInstructorVm(courseId);
        CourseResponse.FindById courseDTO = courseService.findById(courseId);
        User user = userDetails.getUser();
        model.addAttribute("user", user);
        model.addAttribute("instructorVms", instructorVms);
        model.addAttribute("courseDTO", courseDTO);
        return "course/instructorVm";
    }

    //강좌 취소
    @GetMapping("/{courseId}/cancel")
    public String cancel(@PathVariable Long courseId, Model model, @AuthenticationPrincipal CustomUserDetails userDetails){
        User user = userDetails.getUser();
        model.addAttribute("user", user);
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
    public String createForm(Model model, @AuthenticationPrincipal CustomUserDetails userDetails){
        User user = userDetails.getUser();
        model.addAttribute("saveDTO", new CourseRequest.SaveDTO());
        model.addAttribute("user", user);
        return "course/create";
    }

    //수강생 관리 페이지
    @GetMapping("/{courseId}/students")
    public String findStudents(@PathVariable Long courseId, Model model, @AuthenticationPrincipal CustomUserDetails userDetails){
        User user = userDetails.getUser();
        CourseResponse.FindStudentsDTO studentDTOs = courseService.findStudents(courseId, user);
        CourseResponse.FindById courseDTO = courseService.findById(courseId);
        model.addAttribute("user", user);
        model.addAttribute("studentDTOs", studentDTOs);
        model.addAttribute("courseDTO", courseDTO);
        return "course/students";
    }

    //설정 및 관리 페이지
    @GetMapping("/{courseId}/setting")
    public String setting(@PathVariable Long courseId, Model model, @AuthenticationPrincipal CustomUserDetails userDetails){
        User user = userDetails.getUser();
        CourseResponse.FindById courseDTO = courseService.findById(courseId);
        CourseResponse.FindStudentsDTO studentDTOs = courseService.findStudents(courseId, user);
        CourseResponse.FindUnacceptedUserDTO unacceptedUserDTOs = courseService.findApplications(courseId);
        model.addAttribute("user", user);
        model.addAttribute("courseDTO", courseDTO);
        model.addAttribute("studentDTOs", studentDTOs);
        model.addAttribute("unacceptedUserDTOs", unacceptedUserDTOs);
        return "course/setting";
    }

    //강좌 신청 수락
    @PostMapping("/{courseId}/application/{userId}/accept")
    public String accept(@PathVariable Long courseId, @PathVariable Long userId,
                         @AuthenticationPrincipal CustomUserDetails userDetails){
        courseService.accept(courseId, userId, userDetails.getUser());
        return "redirect:/courses/{courseId}/setting";
    }

    //강좌 신청 거절
    @PostMapping("/{courseId}/application/{userId}/reject")
    public String reject(@PathVariable Long courseId, @PathVariable Long userId,
                         @AuthenticationPrincipal CustomUserDetails userDetails){
        courseService.reject(courseId, userId, userDetails.getUser());
        return "redirect:/courses/{courseId}/setting";
    }

    //강좌 삭제
    @PostMapping("/{courseId}/delete")
    public String delete(@PathVariable Long courseId, @AuthenticationPrincipal CustomUserDetails userDetails){
        courseService.delete(courseId, userDetails.getUser());
        return "redirect:/courses";
    }
}
