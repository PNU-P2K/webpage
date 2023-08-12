package com.example.p2k.course;

import com.example.p2k.post.Category;
import jakarta.validation.constraints.NotBlank;
import lombok.Getter;
import lombok.Setter;

public class CourseRequest {

    @Getter
    @Setter
    public static class SaveDTO{

        @NotBlank(message = "강좌 이름은 필수 입력 값입니다.")
        private String name;

        @NotBlank(message = "설명은 필수 입력 값입니다.")
        private String description;
    }

    @Getter
    @Setter
    public static class ApplyDTO{

        @NotBlank(message = "강좌 이름은 필수 입력 값입니다.")
        private String name;

        @NotBlank(message = "설명은 필수 입력 값입니다.")
        private String description;
    }

    @Getter
    @Setter
    public static class PostDTO{

        @NotBlank(message = "제목은 필수 입력 값입니다.")
        private String title;

        @NotBlank(message = "내용은 필수 입력 값입니다.")
        private String content;

        private Category category;
    }
}
