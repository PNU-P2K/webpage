package com.example.p2k.course;

import com.example.p2k.user.User;
import lombok.Getter;
import org.springframework.data.domain.Page;

import java.util.List;
import java.util.stream.Collectors;

public class CourseResponse {

    @Getter
    public static class FindCoursesDTO {

        private final Boolean hasPrevious;
        private final Boolean hasNext;
        private final Boolean isEmpty;
        private final int number;
        private final int totalPages;
        private final int startPage;
        private final int endPage;
        private final List<CourseDTO> courses;
        private static final int cnt = 5;

        public FindCoursesDTO(Page<Course> courses) {
            this.hasPrevious = courses.hasPrevious();
            this.hasNext = courses.hasNext();
            this.isEmpty = courses.isEmpty();
            this.number = courses.getNumber();
            this.totalPages = courses.getTotalPages();
            this.startPage = getStartPage();
            this.endPage = getEndPage();
            this.courses = courses.getContent().stream().map(CourseDTO::new).collect(Collectors.toList());
        }

        public int getStartPage() {
            if(this.getTotalPages() <= cnt){
                return 0;
            }
            int min = 0;
            int start = this.getNumber() - cnt / 2;
            int max = this.getTotalPages() - cnt;
            return Math.min(Math.max(min, start), max);
        }

        public int getEndPage() {
            if(this.getTotalPages() <= cnt){
                return getTotalPages() - 1;
            }
            int max = this.getTotalPages() - 1;
            int end = this.getStartPage() + cnt - 1;
            return Math.min(end, max);
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
