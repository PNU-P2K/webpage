package com.example.p2k.admin;

import com.example.p2k._core.security.CustomUserDetails;
import com.example.p2k.user.User;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

@RequestMapping("/admin")
@RequiredArgsConstructor
@Controller
public class AdminController {

    private final AdminService adminService;
    private final CloudWatchService cloudWatchService;

    @GetMapping("/resources")
    public String manageResources(Model model, @AuthenticationPrincipal CustomUserDetails userDetails){
        User user = userDetails.getUser();
        MetricDataResponse cpuUsageData = cloudWatchService.getCPUUsageData();
        model.addAttribute("user", user);
        model.addAttribute("timestamp", cpuUsageData.getTimestamps());
        model.addAttribute("value", cpuUsageData.getValue());
        return "admin/resources";
    }

    @GetMapping("/vms")
    public String manageVms(Model model, @RequestParam(value = "page", defaultValue = "0") int page,
                            @AuthenticationPrincipal CustomUserDetails userDetails){
        User user = userDetails.getUser();
        AdminResponse.VmsDTO vms = adminService.findAllVms(page);
        model.addAttribute("user", user);
        model.addAttribute("vms", vms);
        return "admin/vms";
    }

    @GetMapping("/users")
    public String manageUsers(Model model, @RequestParam(value = "page", defaultValue = "0") int page,
                              @AuthenticationPrincipal CustomUserDetails userDetails){
        User user = userDetails.getUser();
        AdminResponse.UsersDTO users = adminService.findAllUsers(page);
        model.addAttribute("user", user);
        model.addAttribute("users", users);
        return "admin/users";
    }
}