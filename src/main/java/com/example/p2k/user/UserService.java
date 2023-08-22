package com.example.p2k.user;

import com.example.p2k._core.exception.Exception404;
import com.example.p2k.courseuser.CourseUserRepository;
import com.example.p2k.vm.VmRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Transactional
@RequiredArgsConstructor
@Service
public class UserService {

    private final UserRepository userRepository;
    private final CourseUserRepository courseUserRepository;
    private final VmRepository vmRepository;
    private final BCryptPasswordEncoder bCryptPasswordEncoder;

    @Transactional
    public void save(UserRequest.joinDTO requestDTO) {
        String enPassword = bCryptPasswordEncoder.encode(requestDTO.getPassword1()); // 비밀번호 암호화

        User user = User.builder()
                .email(requestDTO.getEmail())
                .name(requestDTO.getName())
                .password(enPassword)
                .role(requestDTO.getRole())
                .build();

        userRepository.save(user);
    }

    public Boolean findByEmail(String email) {
        User user = userRepository.findByEmail(email).orElseThrow(null);

        if (user==null) {
            return false;
        } else {
            return true;
        }
    }

    public UserResponse.FindByIdDTO findById(Long id){
        User user = userRepository.findById(id).orElseThrow(
                () -> new Exception404("해당 사용자를 찾을 수 없습니다.")
        );
        return new UserResponse.FindByIdDTO(user);
    }

    @Transactional
    public void update(Long id, UserRequest.UpdateDTO requestDTO){
        userRepository.update(id, requestDTO.getEmail(), requestDTO.getName());
    }

    @Transactional
    public void delete(Long id){
        User user = userRepository.findById(id).orElseThrow(
                () -> new Exception404("해당 사용자를 찾을 수 없습니다.")
        );
        courseUserRepository.deleteByUserId(id);
        vmRepository.deleteByUserId(id);
        userRepository.delete(user);
    }
}
