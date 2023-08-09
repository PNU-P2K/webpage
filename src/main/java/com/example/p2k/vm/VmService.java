package com.example.p2k.vm;

import com.example.p2k.user.User;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Transactional(readOnly = true)
@RequiredArgsConstructor
@Service
public class VmService {

    private final VmRepository vmRepository;

    @Transactional
    public List<Vm> findAllByUserId(Long id) {

        List<Vm> vmList = vmRepository.findAllByUserId(id);

        return vmList;
    }
}
