package com.example.p2k.admin;

import com.example.p2k.course.Course;
import com.example.p2k.course.CourseResponse;
import com.example.p2k.user.Role;
import com.example.p2k.user.User;
import com.example.p2k.vm.Vm;
import lombok.Getter;
import org.springframework.data.domain.Page;

import java.time.LocalDate;
import java.util.List;
import java.util.stream.Collectors;

public class AdminResponse {

    @Getter
    public static class UsersDTO {

        private final Boolean hasPrevious;
        private final Boolean hasNext;
        private final Boolean isEmpty;
        private final int number;
        private final int totalPages;
        private final int startPage;
        private final int endPage;
        private static final int cnt = 5;
        private final List<UserDTO> users;

        public UsersDTO(Page<User> users) {
            this.hasPrevious = users.hasPrevious();
            this.hasNext = users.hasNext();
            this.isEmpty = users.isEmpty();
            this.number = users.getNumber();
            this.totalPages = users.getTotalPages();
            this.startPage = getStartPage();
            this.endPage = getEndPage();
            this.users = users.stream().map(UserDTO::new).collect(Collectors.toList());
        }

        public int getStartPage() {
            if(this.getTotalPages() <= cnt){
                return 0;
            }
            int min = 0;
            int start = this.getNumber() - cnt / 2;
            int max = this.getTotalPages() - cnt;
            return Math.min(Math.max(min, start), max);
        }

        public int getEndPage() {
            if(this.getTotalPages() <= cnt){
                return getTotalPages() - 1;
            }
            int max = this.getTotalPages() - 1;
            int end = this.getStartPage() + cnt - 1;
            return Math.min(end, max);
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

        private final Boolean hasPrevious;
        private final Boolean hasNext;
        private final Boolean isEmpty;
        private final int number;
        private final int totalPages;
        private final int startPage;
        private final int endPage;
        private static final int cnt = 5;
        private final List<VmDTO> vms;

        public VmsDTO(Page<Vm> vms) {
            this.hasPrevious = vms.hasPrevious();
            this.hasNext = vms.hasNext();
            this.isEmpty = vms.isEmpty();
            this.number = vms.getNumber();
            this.totalPages = vms.getTotalPages();
            this.startPage = getStartPage();
            this.endPage = getEndPage();
            this.vms = vms.stream().map(VmDTO::new).collect(Collectors.toList());
        }

        public int getStartPage() {
            if(this.getTotalPages() <= cnt){
                return 0;
            }
            int min = 0;
            int start = this.getNumber() - cnt / 2;
            int max = this.getTotalPages() - cnt;
            return Math.min(Math.max(min, start), max);
        }

        public int getEndPage() {
            if(this.getTotalPages() <= cnt){
                return getTotalPages() - 1;
            }
            int max = this.getTotalPages() - 1;
            int end = this.getStartPage() + cnt - 1;
            return Math.min(end, max);
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