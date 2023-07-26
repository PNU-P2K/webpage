package com.example.p2k.Vm;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

@RequiredArgsConstructor
@Controller
public class VmController {

    @GetMapping("/")
    public String main() {
        return "index";
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
