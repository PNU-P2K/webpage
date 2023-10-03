package com.example.p2k.vm;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

import java.util.List;
import java.util.stream.Collectors;

public class VmResponse {

    @Getter
    public static class FindByIdDTO {
        private final Long id;
        private final String name;
//        private final int port;
//        private final String state;
//        private final Boolean scope;
//        private final Boolean control;
//        private final String imageId;
//        private final String key;

        public FindByIdDTO(Vm vm) {
            this.id = vm.getId();
            this.name = vm.getVmname();
//            this.port = vm.getPort();
//            this.state = vm.getState();
//            this.scope = vm.getScope();
//            this.control = vm.getControl();
//            this.imageId = vm.getImageId();
//            this.key = vm.getVmKey();
        }
    }

    @Getter
    public static class FindAllDTO {
        private final List<VmDTO> vms;

        public FindAllDTO(List<Vm> vms) {
            this.vms = vms.stream().map(VmDTO::new).collect(Collectors.toList());
        }

        @Getter
        public class VmDTO{

            private final Long id;
            private final String name;
            private final int port;
            private final String state;
            private final Boolean scope;
            private final Boolean control;
            private final String imageId;
            private final String key;
            private final String courseName;
            private final String creator;
            private final String description;

            public VmDTO(Vm vm) {
                this.id = vm.getId();
                this.name = vm.getVmname();
                this.port = vm.getPort();
                this.state = vm.getState();
                this.scope = vm.getScope();
                this.control = vm.getControl();
                this.imageId = vm.getImageId();
                this.key = vm.getVmKey();
                if (vm.getCourse()!=null) {
                    this.courseName = vm.getCourse().getName();
                } else {
                    this.courseName = null;
                }
                this.creator = vm.getUser().getName();
                this.description = vm.getDescription();
            }
        }
    }

    @Getter
    public static class CreateDTO {

        private final String errorMsg;

        public CreateDTO(String errorMsg) {
            this.errorMsg = errorMsg;
        }
    }

    @Getter
    public static class UpdateDTO {

        private Long id;
        private String name;
        private String description;
        private Long courseId;

        public UpdateDTO(Vm vm) {
            this.id = vm.getId();
            this.name = vm.getVmname();
            this.description = vm.getDescription();
            this.courseId = vm.getCourse().getId();
        }
    }
}
