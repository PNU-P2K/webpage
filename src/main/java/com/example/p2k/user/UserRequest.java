package com.example.p2k.user;

import lombok.Getter;
import lombok.Setter;

public class UserRequest {

    @Getter
    @Setter
    public static class JoinDTO {

        private String email;
        private String name;
        private String password;
        private String password1;
        private Role role = Role.ROLE_STUDENT;
    }

    @Getter
    @Setter
    public static class LoginDTO {

        private String email;
        private String password;
    }

    @Getter
    @Setter
    public static class UpdateDTO {

        private String email;
        private String name;
    }

    @Getter
    @Setter
    public static class ResetDTO {

        private String email;
        private String password;
        private String passwordConf;
    }
}
