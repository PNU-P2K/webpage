package com.example.p2k.admin;

import com.example.p2k._core.security.CustomUserDetails;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

@RequestMapping("/admin")
@RequiredArgsConstructor
@Controller
public class AdminController {

    private final AdminService adminService;
    private final CloudWatchService cloudWatchService;

    @GetMapping("/resources")
    public String manageResources(Model model, @AuthenticationPrincipal CustomUserDetails userDetails){
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
        model.addAttribute("user", userDetails.getUser());
        return "admin/resources";
    }

    @GetMapping("/vms")
    public String manageVms(Model model, @RequestParam(value = "page", defaultValue = "0") int page,
                            @AuthenticationPrincipal CustomUserDetails userDetails){
        AdminResponse.VmsDTO vms = adminService.findAllVms(page);
        model.addAttribute("vms", vms);
        model.addAttribute("user", userDetails.getUser());
        return "admin/vms";
    }

    @GetMapping("/users")
    public String manageUsers(Model model, @RequestParam(value = "page", defaultValue = "0") int page,
                              @AuthenticationPrincipal CustomUserDetails userDetails){
        AdminResponse.UsersDTO users = adminService.findAllUsers(page);
        model.addAttribute("users", users);
        model.addAttribute("user", userDetails.getUser());
        return "admin/users";
    }

    @PostMapping("/users/{userId}/accept")
    public String acceptUser(@PathVariable Long userId, @AuthenticationPrincipal CustomUserDetails userDetails){
        adminService.accept(userId, userDetails.getUser());
        return "redirect:/admin/users";
    }

    @GetMapping("/setting")
    public String setting(Model model, @AuthenticationPrincipal CustomUserDetails userDetails){
        AdminResponse.SettingDTO constants = adminService.getConstants();
        model.addAttribute("updateDTO", constants);
        model.addAttribute("user", userDetails.getUser());
        return "admin/setting";
    }

    @PostMapping("/setting")
    public String updateSetting(@Valid @ModelAttribute AdminRequest.UpdateDTO updateDTO, Error errors,
                                @AuthenticationPrincipal CustomUserDetails userDetails){
        adminService.updateSetting(updateDTO, userDetails.getUser());
        return "redirect:/admin/setting";
    }
}