package com.example.p2k.vm;

import com.example.p2k._core.security.CustomUserDetails;
import com.example.p2k.user.User;
import com.example.p2k.user.UserResponse;
import com.example.p2k.user.UserService;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.client.RestTemplate;

@RequiredArgsConstructor
@Controller
@RequestMapping("/vm")
public class VmController {

    private final VmService vmService;
    private final UserService userService;

    private final BCryptPasswordEncoder bCryptPasswordEncoder;
    private final RestTemplate restTemplate;
    private final ObjectMapper ob = new ObjectMapper();

    @GetMapping
    public String vm(@AuthenticationPrincipal CustomUserDetails userDetails, Model model) {
        UserResponse.FindByIdDTO user = userService.findById(userDetails.getUser().getId());
        VmResponse.FindAllDTO vmList = vmService.findAllByUserId(userDetails.getUser().getId());
        model.addAttribute("user", user);
        model.addAttribute("vm", vmList);
        return "vm/vm";
    }

    @GetMapping("/create")
    public String createForm(Model model) {
        model.addAttribute("vm", new VmRequest.CreateDTO());
        return "vm/create";
    }

    @PostMapping("/create")
    public String create(@AuthenticationPrincipal CustomUserDetails userDetails,
                         @ModelAttribute("vm") VmRequest.CreateDTO requestDTO){
        vmService.create(userDetails.getUser(), requestDTO);
        return "redirect:/vm";
    }

    @PostMapping("/{id}/access")
    public String access(@PathVariable Long id){
        vmService.access(id);
        return "redirect:/vm";
    }

    @PostMapping("/{id}/save")
    public String commit(@PathVariable Long id){
        vmService.commit(id);
        return "redirect:/vm";
    }

    @GetMapping("/load")
    public String loadForm(Model model) {
        model.addAttribute("loadDTO", new VmRequest.LoadDTO());
        return "vm/load";
    }

    @PostMapping("/load")
    public String load(@ModelAttribute VmRequest.LoadDTO requestDTO,
                       @AuthenticationPrincipal CustomUserDetails userDetails){
        vmService.load(requestDTO, userDetails.getUser());
        return "redirect:/vm";
    }

    @PostMapping("/{id}/delete")
    public String delete(@PathVariable Long id) {
        vmService.delete(id);
        return "redirect:/vm";
    }

        /*@PostMapping("/create")
    public String create(@AuthenticationPrincipal CustomUserDetails userDetails,
                         @ModelAttribute("vm") VmRequest.CreateDTO requestDTO) throws Exception {

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

    /*    @PostMapping("/delete/{id}")
    public String delete(@AuthenticationPrincipal CustomUserDetails userDetails, @PathVariable Long id) throws Exception {

        String baseUrl = "http://localhost:5000/delete";

        Vm vm = vmService.findById(id);

        VmRequest.deleteDTOsb requestDTOsb = new VmRequest.deleteDTOsb();
        requestDTOsb.setPort(vm.getPort());
        requestDTOsb.setContainerId(vm.getContainerKey());

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);

        String jsonStr = ob.writeValueAsString(requestDTOsb);

        HttpEntity<String> entity = new HttpEntity<String>(jsonStr ,headers);

        System.out.println("entity.getHeaders() = " + entity.getHeaders());
        System.out.println("entity.getBody() = " + entity.getBody());

        restTemplate.postForEntity(baseUrl, entity, VmResponse.deleteDTOfl.class);

        vmService.delete(id);

        return "redirect:/vm/access";
    }*/
}
