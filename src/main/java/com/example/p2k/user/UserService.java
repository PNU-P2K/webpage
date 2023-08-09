package com.example.p2k.user;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Transactional
@RequiredArgsConstructor
@Service
public class UserService {

    private final UserRepository userRepository;

    public void save(User user) {
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

}
