package com.example.p2k.admin;

import com.example.p2k._core.util.PageData;
import com.example.p2k.user.Role;
import com.example.p2k.user.User;
import com.example.p2k.vm.Vm;
import lombok.Getter;
import org.springframework.data.domain.Page;

import java.time.LocalDate;
import java.util.List;

public class AdminResponse {

    @Getter
    public static class UsersDTO {

        private final PageData pageData;
        private final List<UserDTO> users;

        public UsersDTO(Page<User> users, int size) {
            this.pageData = new PageData(
                    users.hasPrevious(),
                    users.hasNext(),
                    users.isEmpty(),
                    users.getNumber(),
                    users.getTotalPages(),
                    size
            );
            this.users = users.stream().map(UserDTO::new).toList();
        }

        @Getter
        public class UserDTO {
            private final Long id;
            private final String name;
            private final String email;
            private final Role role;
            private final Boolean pending;
            private final int vmNum;
            private final int courseNum;
            private final int postNum;
            private final int replyNum;
            private final LocalDate createdDate;

            public UserDTO(User user) {
                this.id = user.getId();
                this.name = user.getName();
                this.email = user.getEmail();
                this.role = user.getRole();
                this.pending = user.getPending();
                this.vmNum = user.getVms().size();
                this.courseNum = user.getCourseUsers().size();
                this.postNum = user.getPosts().size();
                this.replyNum = user.getReplies().size();
                this.createdDate = user.getCreatedDate() != null ? user.getCreatedDate().toLocalDate() : null;
            }
        }
    }

    @Getter
    public static class VmsDTO {

        private final PageData pageData;
        private final List<VmDTO> vms;

        public VmsDTO(Page<Vm> vms, int size) {
            this.pageData = new PageData(
                    vms.hasPrevious(),
                    vms.hasNext(),
                    vms.isEmpty(),
                    vms.getNumber(),
                    vms.getTotalPages(),
                    size
            );
            this.vms = vms.stream().map(VmDTO::new).toList();
        }

        @Getter
        public class VmDTO {
            private final Long id;
            private final String name;
            private final String createdBy;
            private final int port;
            private final String course;
            private final Boolean scope;
            private final Boolean control;
            private final String state;
            private final LocalDate createdDate;

            public VmDTO(Vm vm) {
                this.id = vm.getId();
                this.name = vm.getVmname();
                this.createdBy = vm.getUser().getName();
                this.port = vm.getPort();
                this.course = vm.getCourse().getName();
                this.scope = vm.getScope();
                this.control = vm.getControl();
                this.state = vm.getState();
                this.createdDate = vm.getCreatedDate() != null ? vm.getCreatedDate().toLocalDate() : null;
            }
        }
    }
}