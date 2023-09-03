package com.example.p2k.vm;

import lombok.Getter;
import lombok.Setter;

public class VmRequest {

    @Getter @Setter
    public static class CreateDTO {

        private String vmname;
        private String password;
        private Boolean scope;
        private Boolean control;
    }

    @Getter
    @Setter
    public static class LoadDTO{

        private String name;
        private String password;
        private String key;
        private Boolean scope;
        private Boolean control;
    }
}
