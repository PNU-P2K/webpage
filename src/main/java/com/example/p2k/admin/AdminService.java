package com.example.p2k.admin;

import com.example.p2k._core.exception.Exception404;
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

    public static final int USER_PAGE_SIZE = 10;
    public static final int VM_PAGE_SIZE = 10;
    public static final int USER_PAGINATION_SIZE = 5;
    public static final int VM_PAGINATION_SIZE = 5;

    private final UserRepository userRepository;
    private final VmRepository vmRepository;

    public AdminResponse.UsersDTO findAllUsers(int page){
        Pageable pageable = getPageable(page, "name", USER_PAGE_SIZE);
        Page<User> users = userRepository.findAll(pageable);
        return new AdminResponse.UsersDTO(users, USER_PAGINATION_SIZE);
    }

    public AdminResponse.VmsDTO findAllVms(int page){
        Pageable pageable = getPageable(page, "vmname", VM_PAGE_SIZE);
        Page<Vm> vms = vmRepository.findAll(pageable);
        return new AdminResponse.VmsDTO(vms, VM_PAGINATION_SIZE);
    }

    public void accept(Long id){
        User user = userRepository.findById(id).orElseThrow(
                () -> new Exception404("해당 사용자를 찾을 수 없습니다.")
        );
        user.updatePending(false);
    }

    private static Pageable getPageable(int page, String orderBy, int size) {
        List<Sort.Order> sorts = new ArrayList<>();
        sorts.add(Sort.Order.asc(orderBy));
        return PageRequest.of(page, size, Sort.by(sorts));
    }
}