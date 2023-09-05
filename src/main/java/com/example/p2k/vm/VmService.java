package com.example.p2k.vm;

import com.example.p2k.user.User;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.client.RestTemplate;

import java.util.List;

@Slf4j
@Transactional(readOnly = true)
@RequiredArgsConstructor
@Service
public class VmService {

    private final VmRepository vmRepository;

    private final BCryptPasswordEncoder bCryptPasswordEncoder;
    private final RestTemplate restTemplate;
    private final ObjectMapper ob = new ObjectMapper();
    private int portnum = 6080;

    private final String baseURL = "http://43.201.20.193:5000";

    @Transactional
    public VmResponse.FindAllDTO findAllByUserId(Long id) {
        List<Vm> vmList = vmRepository.findAllByUserId(id);
        return new VmResponse.FindAllDTO(vmList);
    }

    @Transactional
    public void save(Vm vm) {
        vmRepository.save(vm);
    }

    @Transactional
    public void create(User user, VmRequest.CreateDTO requestDTO) throws Exception {

        // 요청을 보낼 flask url
        String url = baseURL+"/create";

        // 암호화한 key
        String key = bCryptPasswordEncoder.encode(user.getPassword());

        // requestDTO header 설정
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);

        // requestDTO body 만들기 - port, pwd
        VmRequestStF.createDTO requestDTOStF = new VmRequestStF.createDTO();
        requestDTOStF.setId(user.getId());
        requestDTOStF.setPort(portnum);
        requestDTOStF.setPassword(requestDTO.getPassword());

        // json을 string으로
        String jsonStr = ob.writeValueAsString(requestDTOStF);

        // header, body로 requestDTO 만들기
        HttpEntity<String> entity = new HttpEntity<>(jsonStr, headers);

        // restTemplate 이용해 요청 보내고 응답 받아옴
        ResponseEntity<?> response = restTemplate.postForEntity(url, entity, VmResponseFtS.createDTO.class);
        String responseBody = ob.writeValueAsString(response.getBody());
        VmResponseFtS.createDTO res = ob.readValue(responseBody, VmResponseFtS.createDTO.class);
        System.out.println("res = " + res.getContainerId());
        System.out.println("res.getImageId() = " + res.getImageId());

        // 임시로 제어권은 true로 설정
        if (requestDTO.getControl()==null) {
            requestDTO.setControl(true);
        }

        // flask에서 받은 응답으로 가상환경 생성하고 저장
        Vm vm = Vm.builder()
                .vmname(requestDTO.getVmname())
                .password(requestDTO.getPassword())
                .scope(requestDTO.getScope().booleanValue())
                .control(requestDTO.getControl().booleanValue())
                .user(user)
                .port(portnum)
                .containerId(res.getContainerId())
                .imageId(res.getImageId())
                .state("stop")
                .build();

        vmRepository.save(vm);
        portnum+=1;
    }

    @Transactional
    public void load(User user, VmRequest.LoadDTO requestDTO) throws Exception {

        // 요청을 보낼 flask url
        String url = baseURL+"/load";

        // 암호화한 key
        String key = bCryptPasswordEncoder.encode(user.getPassword());

        // requestDTO header 설정
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);

        // requestDTO body 만들기 - port, pwd
        VmRequestStF.loadDTO requestDTOStF = new VmRequestStF.loadDTO();
        requestDTOStF.setId(user.getId());
        requestDTOStF.setPort(portnum);
        requestDTOStF.setPassword(requestDTO.getPassword());
        requestDTOStF.setKey(requestDTO.getKey()); // 로드할 이미지의 id (지금은 key라고함)
        String jsonStr = ob.writeValueAsString(requestDTOStF);

        // header, body로 requestDTO 만들기
        HttpEntity<String> entity = new HttpEntity<>(jsonStr, headers);

        // restTemplate 이용해 요청 보내고 응답 받아옴
        ResponseEntity<?> response = restTemplate.postForEntity(url, entity, VmResponseFtS.loadDTO.class);
        String responseBody = ob.writeValueAsString(response.getBody());
        VmResponseFtS.loadDTO res = ob.readValue(responseBody, VmResponseFtS.loadDTO.class);
        System.out.println("res = " + res.getContainerId());
        System.out.println("res.getImageId() = " + res.getImageId());

        // 임시로 제어권은 true로 설정
        if (requestDTO.getControl()==null) {
            requestDTO.setControl(true);
        }

        // flask에서 받은 응답으로 가상환경 생성하고 저장
        Vm vm = Vm.builder()
                .vmname(requestDTO.getName())
                .password(requestDTO.getPassword())
                .scope(requestDTO.getScope().booleanValue())
                .control(requestDTO.getControl().booleanValue())
                .user(user)
                .port(portnum)
                .containerId(res.getContainerId())
                .imageId(res.getImageId())
                .state("stop")
                .vmKey(key)
                .build();

        vmRepository.save(vm);
        portnum+=1;

    }

    @Transactional
    public void start(Long id) throws Exception {

        // 요청을 보낼 flask url
        String url = baseURL+"/start";

        // 시작할 가상환경 찾기
        Vm vm = vmRepository.findById(id).orElse(null);

        // requestDTO header 설정
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);

        // requestDTO body 만들기 - port, 컨테이너id
        VmRequestStF.startDTO requestDTOStF = new VmRequestStF.startDTO();
        requestDTOStF.setPort(vm.getPort());
        requestDTOStF.setContainerId(vm.getContainerId());
        String jsonStr = ob.writeValueAsString(requestDTOStF);

        // header, body로 requestDTO 만들기
        HttpEntity<String> entity = new HttpEntity<>(jsonStr ,headers);

        // flask로 요청보냄
        restTemplate.postForEntity(url, entity, VmResponseFtS.startDTO.class);

        vm.updateState("running");
    }

    @Transactional
    public void stop(Long id) throws Exception {

        // 요청을 보낼 flask url
        String url = baseURL+"/stop";

        // 중지할 가상환경 찾기
        Vm vm = vmRepository.findById(id).orElse(null);

        // requestDTO header 설정
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);

        // requestDTO body 만들기 - port, 컨테이너id
        VmRequestStF.stopDTO requestDTOStF = new VmRequestStF.stopDTO();
        requestDTOStF.setPort(vm.getPort());
        requestDTOStF.setContainerId(vm.getContainerId());
        String jsonStr = ob.writeValueAsString(requestDTOStF);

        // header, body로 requestDTO 만들기
        HttpEntity<String> entity = new HttpEntity<>(jsonStr ,headers);

        // flask로 요청보냄
        restTemplate.postForEntity(url, entity, VmResponseFtS.stopDTO.class);

        vm.updateState("stop");
    }

    @Transactional
    public void save(User user, Long id) throws Exception {

        // 요청을 보낼 flask url
        String url = baseURL+"/save";

        // 저장할 가상환경 찾기
        Vm vm = vmRepository.findById(id).orElse(null);

        // requestDTO header 설정
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);

        // requestDTO body 만들기 - port, 컨테이너id
        VmRequestStF.saveDTO requestDTOStF = new VmRequestStF.saveDTO();
        requestDTOStF.setId(user.getId());
        requestDTOStF.setPort(vm.getPort());
        requestDTOStF.setPwd(vm.getPassword());
        requestDTOStF.setImageId(vm.getImageId());
        requestDTOStF.setContainerId(vm.getContainerId());
        String jsonStr = ob.writeValueAsString(requestDTOStF);

        // header, body로 requestDTO 만들기
        HttpEntity<String> entity = new HttpEntity<>(jsonStr ,headers);

        // flask로 요청보냄
        ResponseEntity<?> response = restTemplate.postForEntity(url, entity, VmResponseFtS.saveDTO.class);
        String responseBody = ob.writeValueAsString(response.getBody());
        VmResponseFtS.saveDTO res = ob.readValue(responseBody, VmResponseFtS.saveDTO.class);
        System.out.println("res = " + res);
        System.out.println("res.getContainerId() = " + res.getContainerId());
        System.out.println("res.getImageId() = " + res.getImageId());

        // 새로운 containerid, imageid 업데이트
        vm.updateContaierId(res.getContainerId());
        vm.updateImageId(res.getImageId());
    }

    @Transactional
    public void delete(User user, Long id) throws Exception {

        String url = baseURL+"/delete";

        Vm vm = vmRepository.findById(id).orElse(null);

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);

        VmRequestStF.deleteDTO requestDTOStF = new VmRequestStF.deleteDTO();
        requestDTOStF.setId(user.getId());
        requestDTOStF.setPort(vm.getPort());
        requestDTOStF.setContainerId(vm.getContainerId());
        requestDTOStF.setImageId(vm.getImageId());
        String jsonStr = ob.writeValueAsString(requestDTOStF);

        HttpEntity<String> entity = new HttpEntity<String>(jsonStr ,headers);

        restTemplate.postForEntity(url, entity, VmResponseFtS.deleteDTO.class);

        vmRepository.deleteById(id);
    }
}
