package com.example.p2k;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class HomeController {

    @GetMapping
    public String index(){
        return "landing";
    }

    @GetMapping("/home")
    public String home(){
        return "home";
    }
}
