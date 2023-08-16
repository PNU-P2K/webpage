package com.example.p2k.course;

import com.example.p2k._core.security.CustomUserDetails;
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

    //강좌 검색
    @PostMapping("/apply/search")
    public String search(@RequestParam String keyword, Model model){
        CourseResponse.FindCoursesDTO courseDTOs = courseService.findBySearch(keyword);
        model.addAttribute("courseDTOs", courseDTOs);
        return "course/student/apply";
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
}
