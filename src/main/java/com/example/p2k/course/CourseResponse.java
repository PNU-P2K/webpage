package com.example.p2k.course;

import com.example.p2k.user.User;
import lombok.Getter;

import java.util.List;
import java.util.stream.Collectors;

public class CourseResponse {

    @Getter
    public static class FindCoursesDTO{

        private final List<CourseDTO> courses;

        public FindCoursesDTO(List<Course> courses) {
            this.courses = courses.stream().map(CourseDTO::new).collect(Collectors.toList());
        }

        @Getter
        public class CourseDTO{
            private final Long id;
            private final String name;
            private final String description;

            public CourseDTO(Course course) {
                this.id = course.getId();
                this.name = course.getName();
                this.description = course.getDescription();
            }
        }
    }

    @Getter
    public static class FindById{

        private final Long id;
        private final String name;

        public FindById(Course course) {
            this.id = course.getId();
            this.name = course.getName();
        }
    }

    @Getter
    public static class FindMyVmDTO{}

    @Getter
    public static class FindInstructorVmDTO{}

    @Getter
    public static class FindStudentsDTO{

        private final List<FindStudentsDTO.UserDTO> students;

        public FindStudentsDTO(List<User> students) {
            this.students = students.stream().map(FindStudentsDTO.UserDTO::new).collect(Collectors.toList());
        }

        @Getter
        public class UserDTO{
            private final Long id;
            private final String name;

            public UserDTO(User user) {
                this.id = user.getId();
                this.name = user.getName();
            }
        }
    }

    @Getter
    public static class FindUnacceptedUserDTO {

        private final List<UnAcceptedUserDTO> students;

        public FindUnacceptedUserDTO(List<User> students) {
            this.students = students.stream().map(UnAcceptedUserDTO::new).collect(Collectors.toList());
        }

        @Getter
        public class UnAcceptedUserDTO{

            private final Long id;
            private final String name;

            public UnAcceptedUserDTO(User user) {
                this.id = user.getId();
                this.name = user.getName();
            }
        }
    }
}
