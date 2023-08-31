package com.example.p2k.vm;

import com.example.p2k._core.exception.Exception404;
import com.example.p2k.user.User;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.jcraft.jsch.ChannelExec;
import com.jcraft.jsch.JSch;
import com.jcraft.jsch.Session;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.client.RestTemplate;

import java.io.InputStream;
import java.util.List;

@Slf4j
@Transactional(readOnly = true)
@RequiredArgsConstructor
@Service
public class VmService {

    private final VmRepository vmRepository;

    private final String host = "ec2-13-125-207-170.ap-northeast-2.compute.amazonaws.com";
    private final String privateKey = "C:\\Users\\kihae\\.ssh\\p2k";

    private final BCryptPasswordEncoder bCryptPasswordEncoder;
    private final RestTemplate restTemplate;
    private final ObjectMapper ob = new ObjectMapper();
    private int portnum = 6080;

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
        String baseUrl = "http://localhost:5000/create";

        // 암호화한 key
        String key = bCryptPasswordEncoder.encode(user.getPassword());

        // requestDTO header 설정
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);

        // requestDTO body 만들기 - port, pwd
        VmRequest.createDTOsb requestDTOsb = new VmRequest.createDTOsb();
        requestDTOsb.setPort(portnum);
        requestDTOsb.setPassword(requestDTO.getPassword());

        // json을 string으로
        String jsonStr = ob.writeValueAsString(requestDTOsb);

        // header, body로 requestDTO 만들기
        HttpEntity<String> entity = new HttpEntity<>(jsonStr, headers);

        // restTemplate 이용해 요청 보내고 응답 받아옴
        ResponseEntity<?> response = restTemplate.postForEntity(baseUrl, entity, VmResponse.createDTOfl.class);
        String responseBody = ob.writeValueAsString(response.getBody());
        VmResponse.createDTOfl res = ob.readValue(responseBody, VmResponse.createDTOfl.class);
        System.out.println("res = " + res);

        // flask에서 받은 응답으로 가상환경 생성하고 저장
        Vm vm = Vm.builder()
                .vmname(requestDTO.getVmname())
                .password(requestDTO.getPassword())
                .scope(requestDTO.getScope().booleanValue())
                .control(requestDTO.getControl().booleanValue())
                .user(user)
                .port(portnum)
                .containerId(res.getContainerId())
                .state("running")
                .vmKey(key)
                .build();

        vmRepository.save(vm);
        portnum+=1;
    }

    @Transactional
    public void start(Long id) throws Exception {

        // 요청을 보낼 flask url
        String baseUrl = "http://localhost:5000/start";

        // 중지할 가상환경 찾기
        Vm vm = vmRepository.findById(id).orElse(null);

        // requestDTO header 설정
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);

        // requestDTO body 만들기 - port, 컨테이너id
        VmRequest.startDTOsb requestDTOsb = new VmRequest.startDTOsb();
        requestDTOsb.setPort(vm.getPort());
        requestDTOsb.setContainerId(vm.getContainerId());
        String jsonStr = ob.writeValueAsString(requestDTOsb);

        // header, body로 requestDTO 만들기
        HttpEntity<String> entity = new HttpEntity<>(jsonStr ,headers);

        // flask로 요청보냄
        restTemplate.postForEntity(baseUrl, entity, VmResponse.startDTOfl.class);

        vm.update("running");
    }

    @Transactional
    public void stop(Long id) throws Exception {

        // 요청을 보낼 flask url
        String baseUrl = "http://localhost:5000/stop";

        // 중지할 가상환경 찾기
        Vm vm = vmRepository.findById(id).orElse(null);

        // requestDTO header 설정
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);

        // requestDTO body 만들기 - port, 컨테이너id
        VmRequest.stopDTOsb requestDTOsb = new VmRequest.stopDTOsb();
        requestDTOsb.setPort(vm.getPort());
        requestDTOsb.setContainerId(vm.getContainerId());
        String jsonStr = ob.writeValueAsString(requestDTOsb);

        // header, body로 requestDTO 만들기
        HttpEntity<String> entity = new HttpEntity<>(jsonStr ,headers);

        // flask로 요청보냄
        restTemplate.postForEntity(baseUrl, entity, VmResponse.stopDTOfl.class);

        vm.update("stop");
    }

    @Transactional
    public void delete(Long id) throws Exception {

        String baseUrl = "http://localhost:5000/delete";

        Vm vm = vmRepository.findById(id).orElse(null);

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);

        VmRequest.deleteDTOsb requestDTOsb = new VmRequest.deleteDTOsb();
        requestDTOsb.setPort(vm.getPort());
        requestDTOsb.setContainerId(vm.getContainerId());
        String jsonStr = ob.writeValueAsString(requestDTOsb);

        HttpEntity<String> entity = new HttpEntity<String>(jsonStr ,headers);

        restTemplate.postForEntity(baseUrl, entity, VmResponse.deleteDTOfl.class);

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
