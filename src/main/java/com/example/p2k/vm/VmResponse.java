package com.example.p2k.vm;

import lombok.Getter;

import java.util.List;
import java.util.stream.Collectors;

public class VmResponse {

    @Getter
    public static class createDTOfl {
        private int port;
        private String containerId;
        private String imageId;
    }

    @Getter
    public static class startDTOfl {

        private int port;
        private String containerId;
    }

    @Getter
    public static class stopDTOfl {

        private int port;
        private String containerId;
    }

    @Getter
    public static class saveDTOfl {
        private String containerId;
        private String imageId;
    }

    @Getter
    public static class deleteDTOfl {

        private int port;
        private String containerId;
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
            private final String key;

            public VmDTO(Vm vm) {
                this.id = vm.getId();
                this.name = vm.getVmname();
                this.port = vm.getPort();
                this.state = vm.getState();
                this.scope = vm.getScope();
                this.control = vm.getControl();
                this.key = vm.getVmKey();
            }
        }
    }

    @Getter
    public static class FindByIdDTO {

        private final int port;
        private final String key;

        public FindByIdDTO(Vm vm) {
            this.port = vm.getPort();
            this.key = vm.getVmKey();
        }
    }
}
