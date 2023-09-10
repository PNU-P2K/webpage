package com.example.p2k.vm;

import com.example.p2k._core.exception.Exception400;
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

import java.io.InputStream;
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
    private final int maxNum = 3;

    private final String baseURL = "http://43.200.182.194:5000";
    //private final String baseURL = "http://localhost:5000";

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

        List<Vm> vms = vmRepository.findAllByUserId(user.getId());
        System.out.println("vms.size() = " + vms.size());
        if (vms.size() > maxNum) {
            System.out.println("가상환경 최대 생성함!" );
            throw new Exception400("가상환경은 최대 3개까지 생성할 수 있습니다.");
        }

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

//    @Transactional
//    public void create(User user, VmRequest.CreateDTO requestDTO) {
//        String vmKey = user.getId() + requestDTO.getVmname();
//        String containerKey = "registry.p2kcloud.com/base/" + vmKey;
//        String password = requestDTO.getPassword();
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
//            String command = createImgCmd("vncdesktop", vmKey) + createContainerCmd(password, vmKey)
//                    + stopContainerCmd(vmKey) + testCmd();
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
//
//            Vm vm = Vm.builder()
//                    .vmname(requestDTO.getVmname())
//                    .password(password)
//                    .scope(requestDTO.getScope().booleanValue())
//                    .control(requestDTO.getControl().booleanValue())
//                    .user(user)
//                    .port(port)
//                    .vmKey(vmKey)
//                    .containerKey(containerKey)
//                    .build();
//
//            vmRepository.save(vm);
//            port++;
//        } catch (Exception e) {
//            e.printStackTrace();
//        }
//    }
//
//    @Transactional
//    public void access(Long id){
//        Vm vm = vmRepository.findById(id).orElseThrow(
//                () -> new Exception404("가상 환경을 찾을 수 없습니다.")
//        );
//        String vmKey = vms.getVmKeys();
//        log.info("vmKey=" + vmKey);
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
//            String command = startContainerCmd(vmKey) + testCmd();
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
//
//
//        } catch (Exception e) {
//            e.printStackTrace();
//        }
//    }
//
//    @Transactional
//    public void commit(Long id){
//        Vm vm = vmRepository.findById(id).orElseThrow(
//                () -> new Exception404("가상 환경을 찾을 수 없습니다.")
//        );
//        String vmKey = vm.getVmKey();
//        log.info("vmKey=" + vmKey);
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
//            String command = commitContainerCmd(vmKey) + saveImgCmd(vmKey) + testCmd();
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
//
//
//        } catch (Exception e) {
//            e.printStackTrace();
//        }
//    }
//
//    @Transactional
//    public void load(VmRequest.LoadDTO requestDTO, User user){
//        Vm findVm = vmRepository.findByVmKey(requestDTO.getKey()).orElseThrow(
//                () -> new Exception404("가상 환경을 찾을 수 없습니다.")
//        );
//        String findVmKey = findVm.getVmKey();
//        String newPassword = requestDTO.getPassword();
//        String newVmKey = findVmKey + "." + user.getId() + requestDTO.getName();
//        String newContainerKey = "registry.p2kcloud.com/base/" + newVmKey;
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
//            String command = createImgCmd(findVmKey, newVmKey)
//                    + createContainerCmd(newPassword, newVmKey)
//                    + stopContainerCmd(newVmKey) + testCmd();
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
//
//            Vm vm = Vm.builder()
//                    .vmname(requestDTO.getName())
//                    .password(newPassword)
//                    .scope(requestDTO.getScope().booleanValue())
//                    .control(requestDTO.getControl().booleanValue())
//                    .user(user)
//                    .port(port)
//                    .vmKey(newVmKey)
//                    .containerKey(newContainerKey)
//                    .build();
//
//            vmRepository.save(vm);
//            port++;
//        } catch (Exception e) {
//            e.printStackTrace();
//        }
//    }
//
//    @Transactional
//    public void delete(Long id) {
//        Vm vm = vmRepository.findById(id).orElseThrow(
//                () -> new Exception404("가상 환경을 찾을 수 없습니다.")
//        );
//        String vmKey = vm.getVmKey();
//        log.info("vmKey=" + vmKey);
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
//            String command = deleteImgCmd(vmKey) + deleteContainerCmd(vmKey) + testCmd();
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
//
//            vmRepository.deleteById(id);
//        } catch (Exception e) {
//            e.printStackTrace();
//        }
//    }
//
//    String createImgCmd(String fromKey, String toKey){
//        return "docker tag registry.p2kcloud.com/base/" + fromKey + " registry.p2kcloud.com/base/" + toKey + "\n";
//    }
//
//    String saveImgCmd(String key){
//        return "docker push registry.p2kcloud.com/base/" + key + "\n";
//    }
//
//    String deleteImgCmd(String key){
//        return "docker rmi registry.p2kcloud.com/base/" + key + "\n";
//    }
//
//    String createContainerCmd(String password, String key){
//        return "docker run -d -p " + port + ":6901 -e VNC_PW='" + password + "' --name " + key + " registry.p2kcloud.com/base/" + key + "\n";
//    }
//
//    String startContainerCmd(String key){
//        return "docker start " + key + "\n";
//    }
//
//    String stopContainerCmd(String key){
//        return "docker stop " + key + "\n";
//    }
//
//    String commitContainerCmd(String key){
//        return "docker commit " + key + " registry.p2kcloud.com/base/" + key + "\n";
//    }
//
//    String deleteContainerCmd(String key){
//        return "docker rm " + key + "\n";
//    }
//
//    String testCmd(){
//        return "echo \"test!\"";
//    }

}
