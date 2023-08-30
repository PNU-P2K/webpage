package com.example.p2k;

import com.example.p2k._core.security.CustomUserDetails;
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

    @GetMapping
    public String index(){
        return "landing";
    }

    @GetMapping("/home")
    public String home(@AuthenticationPrincipal CustomUserDetails userDetails, Model model){
        UserResponse.FindByIdDTO user = userService.findById(userDetails.getUser().getId());
        List<Vm> vmList = vmService.findAllByUserId(userDetails.getUser().getId());
        model.addAttribute("user", user);
        model.addAttribute("vm", vmList);

        return "home";
    }
}
