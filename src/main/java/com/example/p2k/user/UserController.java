package com.example.p2k.user;

import com.example.p2k._core.validator.JoinValidator;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;

@RequiredArgsConstructor
@Controller
@RequestMapping("/user")
public class UserController {

    private final UserService userService;
    private final BCryptPasswordEncoder bCryptPasswordEncoder;
    private final JoinValidator joinValidator;

    // 사용자 회원가입 페이지 - GET
    @GetMapping("/join")
    public String join(Model model) {
        model.addAttribute("user", new UserRequest.joinDTO());
        return "join";
    }

    // 회원가입 페이지의 데이터 POST
    @PostMapping("/join")
    public String join (@ModelAttribute("user") UserRequest.joinDTO requestDTO) {
        String enPassword = bCryptPasswordEncoder.encode(requestDTO.getPassword1()); // 비밀번호 암호화
        System.out.println("user : "+requestDTO.getEmail());

        User user = User.builder()
                .email(requestDTO.getEmail())
                .name(requestDTO.getName())
                .password(enPassword)
                .role(requestDTO.getRole())
                .build();

        userService.save(user);

        return "redirect:/user/login";
    }

    // 로그인 페이지 GET
    @GetMapping("/login")
    public String login(Model model) {
        model.addAttribute("user", new UserRequest.loginDTO());
        return "login";
    }

    @GetMapping("/logout")
    public void logout() {
    }

    @GetMapping("/check/{email}")
    public Boolean email_check(@PathVariable("email") String email) {
        System.out.println("email = " + email);
        return userService.findByEmail(email);
    }

}
