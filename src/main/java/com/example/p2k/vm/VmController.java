package com.example.p2k.vm;

import com.example.p2k._core.security.CustomUserDetails;
import com.example.p2k.user.UserResponse;
import com.example.p2k.user.UserService;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
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

import java.util.List;

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
    public String main(@AuthenticationPrincipal CustomUserDetails userDetails, Model model) {
        UserResponse.FindByIdDTO user = userService.findById(userDetails.getUser().getId());
        List<Vm> vmList = vmService.findAllByUserId(userDetails.getUser().getId());
        model.addAttribute("user", user);
        model.addAttribute("vm", vmList);
        return "vm/access";
    }

    @GetMapping("/create")
    public String create(Model model) {
        model.addAttribute("vm", new VmRequest.createDTO());
        return "vm/create";
    }

    @PostMapping("/create")
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
