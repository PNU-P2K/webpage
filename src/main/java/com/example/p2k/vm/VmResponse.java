package com.example.p2k.vm;

import lombok.Getter;

public class VmResponse {

    @Getter
    public static class createDTOfl {
        private int port;
        private String containerId;
    }

    @Getter
    public static class deleteDTOfl {

        private int port;
        private String containerId;
    }
    
}
