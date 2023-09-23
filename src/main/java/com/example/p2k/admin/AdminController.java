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
        MetricDataResponse cpuUtilization = cloudWatchService.getCPUUtilization();
        MetricDataResponse statusCheckFailedSystem = cloudWatchService.getStatusCheckFailedSystem();
        MetricDataResponse statusCheckFailedInstance = cloudWatchService.getStatusCheckFailedInstance();
        MetricDataResponse networkIn = cloudWatchService.getNetworkIn();
        MetricDataResponse networkOut = cloudWatchService.getNetworkOut();
        MetricDataResponse networkPacketsIn = cloudWatchService.getNetworkPacketsIn();
        MetricDataResponse networkPacketsOut = cloudWatchService.getNetworkPacketsOut();
        MetricDataResponse diskReads = cloudWatchService.getDiskReads();
        MetricDataResponse diskReadOperations = cloudWatchService.getDiskReadOperations();
        MetricDataResponse diskWrites = cloudWatchService.getDiskWrites();
        MetricDataResponse diskWriteOperations = cloudWatchService.getDiskWriteOperations();
        model.addAttribute("user", user);
        model.addAttribute("cpuTimestamp", cpuUtilization.getTimestamps());
        model.addAttribute("cpuValue", cpuUtilization.getValue());
        model.addAttribute("statusSystemTimestamp", statusCheckFailedSystem.getTimestamps());
        model.addAttribute("statusSystemValue", statusCheckFailedSystem.getValue());
        model.addAttribute("statusInstanceTimestamp", statusCheckFailedInstance.getTimestamps());
        model.addAttribute("statusInstanceValue", statusCheckFailedInstance.getValue());
        model.addAttribute("networkInTimestamp", networkIn.getTimestamps());
        model.addAttribute("networkInValue", networkIn.getValue());
        model.addAttribute("networkOutTimestamp", networkOut.getTimestamps());
        model.addAttribute("networkOutValue", networkOut.getValue());
        model.addAttribute("networkPacketsInTimestamp", networkPacketsIn.getTimestamps());
        model.addAttribute("networkPacketsInValue", networkPacketsIn.getValue());
        model.addAttribute("networkPacketsOutTimestamp", networkPacketsOut.getTimestamps());
        model.addAttribute("networkPacketsOutValue", networkPacketsOut.getValue());
        model.addAttribute("diskReadsTimestamp", diskReads.getTimestamps());
        model.addAttribute("diskReadsValue", diskReads.getValue());
        model.addAttribute("diskReadOpsTimestamp", diskReadOperations.getTimestamps());
        model.addAttribute("diskReadOpsValue", diskReadOperations.getValue());
        model.addAttribute("diskWritesTimestamp", diskWrites.getTimestamps());
        model.addAttribute("diskWritesValue", diskWrites.getValue());
        model.addAttribute("diskWriteOpsTimestamp", diskWriteOperations.getTimestamps());
        model.addAttribute("diskWriteOpsValue", diskWriteOperations.getValue());
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