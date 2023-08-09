package com.example.p2k.user;

import lombok.Getter;
import lombok.Setter;

public class UserRequest {

    @Getter @Setter
    public static class joinDTO {

        private String email;
        private String name;
        private String password;
        private String password1;
        private Role role = Role.ROLE_STUDENT;
    }

    @Getter @Setter
    public static class loginDTO {
        private String email;
        private String password;
    }
}
