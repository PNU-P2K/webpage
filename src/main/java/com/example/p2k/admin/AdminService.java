package com.example.p2k.admin;

import com.example.p2k.course.Course;
import com.example.p2k.course.CourseResponse;
import com.example.p2k.user.User;
import com.example.p2k.user.UserRepository;
import com.example.p2k.vm.Vm;
import com.example.p2k.vm.VmRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;

@Transactional(readOnly = true)
@RequiredArgsConstructor
@Service
public class AdminService {

    private final UserRepository userRepository;
    private final VmRepository vmRepository;

    public AdminResponse.UsersDTO findAllUsers(int page){
        List<Sort.Order> sorts = new ArrayList<>();
        sorts.add(Sort.Order.asc("name"));
        Pageable pageable = PageRequest.of(page, 10, Sort.by(sorts));
        Page<User> users = userRepository.findAll(pageable);
        return new AdminResponse.UsersDTO(users);
    }

    public AdminResponse.VmsDTO findAllVms(int page){
        List<Sort.Order> sorts = new ArrayList<>();
        sorts.add(Sort.Order.asc("vmname"));
        Pageable pageable = PageRequest.of(page, 10, Sort.by(sorts));
        Page<Vm> vms = vmRepository.findAll(pageable);
        return new AdminResponse.VmsDTO(vms);
    }
}