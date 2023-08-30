package com.example.p2k.vm;

import com.example.p2k._core.security.CustomUserDetails;
import com.example.p2k.user.UserResponse;
import com.example.p2k.user.UserService;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.jcraft.jsch.ChannelExec;
import com.jcraft.jsch.JSch;
import com.jcraft.jsch.JSchException;
import com.jcraft.jsch.Session;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.client.RestTemplate;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.List;

@Slf4j
@RequiredArgsConstructor
@Controller
@RequestMapping("/vm")
public class VmController {

    private final VmService vmService;
    private final UserService userService;

    private final BCryptPasswordEncoder bCryptPasswordEncoder;

    private final RestTemplate restTemplate;

    private final ObjectMapper ob = new ObjectMapper();

    private int portnum=6080;

    @GetMapping
    public String access(@AuthenticationPrincipal CustomUserDetails userDetails, Model model) {
        UserResponse.FindByIdDTO user = userService.findById(userDetails.getUser().getId());
        List<Vm> vmList = vmService.findAllByUserId(userDetails.getUser().getId());
        model.addAttribute("user", user);
        model.addAttribute("vm", vmList);
        return "vm/vm";
    }

    @GetMapping("/create")
    public String create(@AuthenticationPrincipal CustomUserDetails userDetails, Model model) {
        model.addAttribute("vm", new VmRequest.createDTO());
        return "vm/create";
    }

    @GetMapping("/menu")
    public String menu(@AuthenticationPrincipal CustomUserDetails userDetails, Model model) {
        return "vm/menu";
    }

    /*@PostMapping("/create")
    public String create(@AuthenticationPrincipal CustomUserDetails userDetails,
                         @ModelAttribute("vm") VmRequest.createDTO requestDTO) throws Exception {

        String baseUrl = "http://localhost:5000/create";

        String key = bCryptPasswordEncoder.encode(userDetails.getUser().getPassword());

        VmRequest.createDTOsb requestDTOsb = new VmRequest.createDTOsb();
        requestDTOsb.setPort(portnum);
        requestDTOsb.setPassword(requestDTO.getPassword());

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);

        String jsonStr = ob.writeValueAsString(requestDTOsb);

        HttpEntity<String> entity = new HttpEntity<String>(jsonStr ,headers);

        System.out.println("entity.getHeaders() = " + entity.getHeaders());
        System.out.println("entity.getBody() = " + entity.getBody());

        ResponseEntity<?> response = restTemplate.postForEntity(baseUrl, entity, VmResponse.createDTOfl.class);
        String responseBody = ob.writeValueAsString(response.getBody());
        VmResponse.deleteDTOfl res = ob.readValue(responseBody, VmResponse.deleteDTOfl.class);
        System.out.println("res.getContainerId() = " + res.getContainerId());
        
        Vm vm = Vm.builder()
                .vmname(requestDTO.getVmname())
                .password(requestDTO.getPassword())
                .scope(requestDTO.getScope().booleanValue())
                .control(requestDTO.getControl().booleanValue())
                .user(userDetails.getUser())
                .port(portnum)
                .containerId(res.getContainerId())
                .vmkey(key)
                .build();

        vmService.save(vm);
        portnum+=1;

        return "redirect:/vm/create";
    }*/

    @PostMapping("/create")
    public String create(@AuthenticationPrincipal CustomUserDetails userDetails,
                         @ModelAttribute("vm") VmRequest.createDTO requestDTO) throws JSchException {

        String host = "ec2-13-124-77-175.ap-northeast-2.compute.amazonaws.com";
        String privateKey = "C:\\Users\\kihae\\.ssh\\p2k";
        String userKey = userDetails.getUser().getId() + "-" + requestDTO.getVmname();
        log.info("userkey=" + userKey);

        try {
            // SSH 클라이언트 설정
            JSch jsch = new JSch();
            jsch.addIdentity(privateKey);

            // SSH 세션 열기
            Session session = jsch.getSession("ubuntu", host, 22);
            session.setConfig("StrictHostKeyChecking", "no");
            session.connect();

            // 명령 실행을 위한 채널 열기
            ChannelExec channelExec = (ChannelExec) session.openChannel("exec");
            String command = "docker tag registry.p2kcloud.com/base/vncdesktop " + userKey;

            // 명령어 실행 및 결과 처리
            channelExec.setCommand(command);
            InputStream commandOutput = channelExec.getInputStream();
            channelExec.connect();

            // 명령어 실행 결과를 읽어옴
            StringBuilder output = new StringBuilder();
            byte[] buffer = new byte[1024];
            int bytesRead;

            while ((bytesRead = commandOutput.read(buffer)) > 0) {
                output.append(new String(buffer, 0, bytesRead));
            }

            // 채널 및 세션 닫기
            channelExec.disconnect();
            session.disconnect();

            log.info("Output:\n" + output.toString());
        } catch (Exception e) {
            e.printStackTrace();
        }

        return "redirect:/vm";
    }

    @PostMapping("/delete/{id}")
    public String delete(@AuthenticationPrincipal CustomUserDetails userDetails, @PathVariable Long id) throws Exception {

        String baseUrl = "http://localhost:5000/delete";

        Vm vm = vmService.findById(id);

        VmRequest.deleteDTOsb requestDTOsb = new VmRequest.deleteDTOsb();
        requestDTOsb.setPort(vm.getPort());
        requestDTOsb.setContainerId(vm.getContainerId());

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);

        String jsonStr = ob.writeValueAsString(requestDTOsb);

        HttpEntity<String> entity = new HttpEntity<String>(jsonStr ,headers);

        System.out.println("entity.getHeaders() = " + entity.getHeaders());
        System.out.println("entity.getBody() = " + entity.getBody());

        restTemplate.postForEntity(baseUrl, entity, VmResponse.deleteDTOfl.class);
        
        vmService.delete(id);

        return "redirect:/vm/access";
    }

    @GetMapping("/load")
    public String load() {
        return "vm/load";
    }
}
