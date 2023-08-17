package com.example.p2k.vm;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

public class VmRequest {

    @Getter @Setter
    public static class createDTO {

        private String vmname;

        private String password;
        private Boolean scope;
        private Boolean control;
    }

    @Getter @Setter
    public static class createDTOsb {

        private int port;
        private String password;
    }

    @Getter @Setter
    public static class deleteDTOsb {
        private int port;
        private String containerId;
    }

}
