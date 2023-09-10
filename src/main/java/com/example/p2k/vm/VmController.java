package com.example.p2k.vm;

import com.example.p2k._core.exception.Exception400;
import com.example.p2k._core.security.CustomUserDetails;
import com.example.p2k.user.User;
import com.example.p2k.user.UserResponse;
import com.example.p2k.user.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

@RequiredArgsConstructor
@Controller
@RequestMapping("/vm")
public class VmController {

    private final VmService vmService;
    private final UserService userService;


    @GetMapping
    public String vm(@AuthenticationPrincipal CustomUserDetails userDetails, Model model) {
        UserResponse.FindByIdDTO user = userService.findById(userDetails.getUser().getId());
        VmResponse.FindAllDTO vmList = vmService.findAllByUserId(userDetails.getUser().getId());
        model.addAttribute("user", user);
        model.addAttribute("vm", vmList);
        return "vm/vm";
    }

    // 가상환경 생성 페이지
    @GetMapping("/create")
    public String create(@AuthenticationPrincipal CustomUserDetails userDetails, Model model) {
        User user = userDetails.getUser();
        model.addAttribute("user", user);
        model.addAttribute("vm", new VmRequest.CreateDTO());
        model.addAttribute("vmError", new VmResponse.CreateDTO("false"));
        return "vm/create";
    }

    // 가상환경 생성하기
    @PostMapping("/create")
    public String create(@AuthenticationPrincipal CustomUserDetails userDetails, @ModelAttribute("vm") VmRequest.CreateDTO requestDTO, Model model) throws Exception {
        try {
            vmService.create(userDetails.getUser(), requestDTO);
        } catch (Exception400 e) {
            System.out.println("controller : 에러 발견");
            User user = userDetails.getUser();
            model.addAttribute("user", user);
            model.addAttribute("vmError", new VmResponse.CreateDTO("true"));
            return "vm/create";
        }
        return "redirect:/vm";
    }

    // 가상환경 로드 페이지
    @GetMapping("/load")
    public String load(@AuthenticationPrincipal CustomUserDetails userDetails, Model model) {
        model.addAttribute("user", userDetails.getUser());
        model.addAttribute("loadDTO", new VmRequest.LoadDTO());
        return "/vm/load";
    }

    // 가상환경 로드하기
    @PostMapping("/load")
    public String load(@AuthenticationPrincipal CustomUserDetails userDetails, @ModelAttribute("loadDTO") VmRequest.LoadDTO requestDTO) throws Exception {
        System.out.println("requestDTO = " + requestDTO);
        vmService.load(userDetails.getUser(), requestDTO);
        return "redirect:/vm";
    }

    // 가상환경 실행하기
    @PostMapping("/start/{id}")
    public String start(@PathVariable Long id, @AuthenticationPrincipal CustomUserDetails userDetails) throws Exception {
        vmService.start(id);
        return "redirect:/vm";
    }

    // 가상환경 중지하기
    @PostMapping("/stop/{id}")
    public String stop(@PathVariable Long id, @AuthenticationPrincipal CustomUserDetails userDetails) throws Exception {
        vmService.stop(id);
        return "redirect:/vm";
    }

    // 가상환경 저장하기
    @PostMapping("/save/{id}")
    public String save(@PathVariable Long id, @AuthenticationPrincipal CustomUserDetails userDetails) throws Exception {
        vmService.save(userDetails.getUser(), id);
        return "redirect:/vm";
    }

    // 가상환경 삭제하기
    @PostMapping("/delete/{id}")
    public String delete(@PathVariable Long id, @AuthenticationPrincipal CustomUserDetails userDetails) throws Exception {
        vmService.delete(userDetails.getUser(), id);
        return "redirect:/vm";
    }


//    @PostMapping("/{id}/access")
//    public String access(@PathVariable Long id){
//        vmService.access(id);
//        return "redirect:/vm";
//    }
//
//    @PostMapping("/{id}/save")
//    public String commit(@PathVariable Long id){
//        vmService.commit(id);
//        return "redirect:/vm";
//    }
//
//    @GetMapping("/load")
//    public String loadForm(Model model) {
//        model.addAttribute("loadDTO", new VmRequest.LoadDTO());
//        return "vm/load";
//    }
//
//    @PostMapping("/load")
//    public String load(@ModelAttribute VmRequest.LoadDTO requestDTO,
//                       @AuthenticationPrincipal CustomUserDetails userDetails){
//        vmService.load(requestDTO, userDetails.getUser());
//        return "redirect:/vm";
//    }
//
//    @PostMapping("/{id}/delete")
//    public String delete(@PathVariable Long id) {
//        vmService.delete(id);
//        return "redirect:/vm";
//    }


//    @PostMapping("/create")
//    public String create(@AuthenticationPrincipal CustomUserDetails userDetails,
//                         @ModelAttribute("vm") VmRequest.createDTO requestDTO) throws JSchException {
//
//        String host = "ec2-13-124-77-175.ap-northeast-2.compute.amazonaws.com";
//        String privateKey = "C:\\Users\\kihae\\.ssh\\p2k";
//        String userKey = userDetails.getUser().getId() + "-" + requestDTO.getVmname();
//        log.info("userkey=" + userKey);
//
//        try {
//            // SSH 클라이언트 설정
//            JSch jsch = new JSch();
//            jsch.addIdentity(privateKey);
//
//            // SSH 세션 열기
//            Session session = jsch.getSession("ubuntu", host, 22);
//            session.setConfig("StrictHostKeyChecking", "no");
//            session.connect();
//
//            // 명령 실행을 위한 채널 열기
//            ChannelExec channelExec = (ChannelExec) session.openChannel("exec");
//            String command = "docker tag registry.p2kcloud.com/base/vncdesktop " + userKey;
//
//            // 명령어 실행 및 결과 처리
//            channelExec.setCommand(command);
//            InputStream commandOutput = channelExec.getInputStream();
//            channelExec.connect();
//
//            // 명령어 실행 결과를 읽어옴
//            StringBuilder output = new StringBuilder();
//            byte[] buffer = new byte[1024];
//            int bytesRead;
//
//            while ((bytesRead = commandOutput.read(buffer)) > 0) {
//                output.append(new String(buffer, 0, bytesRead));
//            }
//
//            // 채널 및 세션 닫기
//            channelExec.disconnect();
//            session.disconnect();
//
//            log.info("Output:\n" + output.toString());
//        } catch (Exception e) {
//            e.printStackTrace();
//        }
//
//        return "redirect:/vm";
//    }


//    @PostMapping("/delete/{id}")
//    public String delete(@AuthenticationPrincipal CustomUserDetails userDetails, @PathVariable Long id) throws Exception {
//
//        String baseUrl = "http://localhost:5000/delete";
//
//        Vm vm = vmService.findById(id);
//
//        VmRequest.deleteDTOsb requestDTOsb = new VmRequest.deleteDTOsb();
//        requestDTOsb.setPort(vm.getPort());
//        requestDTOsb.setContainerId(vm.getContainerKey());
//
//        HttpHeaders headers = new HttpHeaders();
//        headers.setContentType(MediaType.APPLICATION_JSON);
//
//        String jsonStr = ob.writeValueAsString(requestDTOsb);
//
//        HttpEntity<String> entity = new HttpEntity<String>(jsonStr ,headers);
//
//        System.out.println("entity.getHeaders() = " + entity.getHeaders());
//        System.out.println("entity.getBody() = " + entity.getBody());
//
//        restTemplate.postForEntity(baseUrl, entity, VmResponse.deleteDTOfl.class);
//
//        vmService.delete(id);
//
//        return "redirect:/vm/access";
//    }
}
