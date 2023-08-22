package com.example.p2k.user;

import lombok.Getter;

public class UserResponse {

    @Getter
    public static class FindByIdDTO {
        private final String email;
        private final String name;

        public FindByIdDTO(User user) {
            this.email = user.getEmail();
            this.name = user.getName();
        }
    }
}
