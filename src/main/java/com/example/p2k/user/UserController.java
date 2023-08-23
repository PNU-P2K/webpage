package com.example.p2k.user;

import com.example.p2k._core.security.CustomUserDetails;
import com.example.p2k._core.validator.JoinValidator;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

@Slf4j
@RequiredArgsConstructor
@Controller
@RequestMapping("/user")
public class UserController {

    private final UserService userService;
    private final JoinValidator joinValidator;

    // 사용자 회원가입 페이지 GET
    @GetMapping("/join")
    public String join(Model model) {
        model.addAttribute("user", new UserRequest.JoinDTO());
        return "user/join";
    }

    // 회원가입 페이지의 데이터 POST
    @PostMapping("/join")
    public String join (@ModelAttribute("user") UserRequest.JoinDTO requestDTO) {
        userService.save(requestDTO);
        return "redirect:/user/login";
    }

    // 로그인 페이지 GET
    @GetMapping("/login")
    public String login(Model model) {
        model.addAttribute("user", new UserRequest.LoginDTO());
        return "user/login";
    }

    //회원 정보 페이지
    @GetMapping("/info")
    public String userInfoForm(@AuthenticationPrincipal CustomUserDetails userDetails, Model model) {
        UserResponse.FindByIdDTO user = userService.findById(userDetails.getUser().getId());
        model.addAttribute("user", user);
        return "user/user-info";
    }

    //회원 정보 수정
    @PostMapping("/info")
    public String update(@ModelAttribute UserRequest.UpdateDTO requestDTO,
                         @AuthenticationPrincipal CustomUserDetails userDetails) {
        userService.update(userDetails.getUser().getId(), requestDTO);
        return "redirect:/user/info";
    }

    //비밀번호 재설정 페이지
    @GetMapping("/reset")
    public String resetPasswordForm(Model model) {
        log.info("비밀번호 재설정 페이지");
        model.addAttribute("resetDTO", new UserRequest.ResetDTO());
        log.info("비밀번호 재설정 페이지 확인");
        return "user/reset";
    }

    //비밀번호 재설정
    @PostMapping("/reset")
    public String resetPassword(@ModelAttribute UserRequest.ResetDTO requestDTO) {
        log.info("비밀번호 재설정");
        userService.resetPassword(requestDTO);
        log.info("비밀번호 재설정 확인");
        return "redirect:/user/login";
    }

    //회원 탈퇴
    @GetMapping("/delete")
    public String delete(@AuthenticationPrincipal CustomUserDetails userDetails) {
        userService.delete(userDetails.getUser().getId());
        return "redirect:/";
    }

    @GetMapping("/logout")
    public void logout() {}

    @GetMapping("/check/{email}")
    public Boolean email_check(@PathVariable("email") String email) {
        System.out.println("email = " + email);
        return userService.findByEmail(email);
    }

}
