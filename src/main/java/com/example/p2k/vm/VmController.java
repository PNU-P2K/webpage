package com.example.p2k.vm;

import com.example.p2k._core.security.CustomUserDetails;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;

import java.util.List;

@RequiredArgsConstructor
@Controller
@RequestMapping("/vm")
public class VmController {

    private final VmService vmService;

    @GetMapping("")
    public String main(@AuthenticationPrincipal CustomUserDetails userDetails, Model model) {
        List<Vm> vmList = vmService.findAllByUserId(userDetails.getUser().getId());
        model.addAttribute("user", userDetails.getUser());
        model.addAttribute("vm", vmList);
        return "index";
    }

    @GetMapping("/{id}")
    public String findById(@PathVariable Long id) {
        return "vm";
    }

    @GetMapping("/create")
    public String create() {
        return "create";
    }

    @GetMapping("/load")
    public String load() {
        return "load";
    }
}
