package com.example.p2k;

import com.example.p2k._core.security.CustomUserDetails;
import com.example.p2k.course.Course;
import com.example.p2k.course.CourseResponse;
import com.example.p2k.course.CourseService;
import com.example.p2k.courseuser.CourseUserRepository;
import com.example.p2k.user.UserResponse;
import com.example.p2k.user.UserService;
import com.example.p2k.vm.Vm;
import com.example.p2k.vm.VmRequest;
import com.example.p2k.vm.VmService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

import java.util.List;

@RequiredArgsConstructor
@Controller
public class HomeController {

    private final VmService vmService;
    private final UserService userService;
    private final CourseService courseService;

    @GetMapping
    public String index(){
        return "landing";
    }

    @GetMapping("/home")
    public String home(@AuthenticationPrincipal CustomUserDetails userDetails, Model model){
        UserResponse.FindByIdDTO user = userService.findById(userDetails.getUser().getId());
        List<Vm> vmList = vmService.findAllByUserId(userDetails.getUser().getId());
        CourseResponse.FindCoursesDTO courses = courseService.findCourses(userDetails.getUser().getId());
        System.out.println("vmList = " + vmList);
        System.out.println("courses = " + courses);
        model.addAttribute("user", user);
        model.addAttribute("vm", vmList);
        model.addAttribute("courses", courses);

        return "home";
    }
}
