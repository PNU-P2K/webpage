package com.example.p2k.post;

import jakarta.validation.constraints.NotBlank;
import lombok.Getter;
import lombok.Setter;

public class PostRequest {

    @Getter
    @Setter
    public static class SaveDTO {

        @NotBlank(message = "제목은 필수 입력 값입니다.")
        private String title;

        @NotBlank(message = "내용은 필수 입력 값입니다.")
        private String content;

        private Boolean open;
    }

    @Getter
    @Setter
    public static class UpdateDTO {

        @NotBlank(message = "제목은 필수 입력 값입니다.")
        private String title;

        @NotBlank(message = "내용은 필수 입력 값입니다.")
        private String content;

        private Boolean open;
    }
}
