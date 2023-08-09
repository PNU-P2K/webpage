package com.example.p2k.course.post;

import com.example.p2k.course.Course;
import com.example.p2k.course.courseuser.CourseUser;
import com.example.p2k.user.BaseTimeEntity;
import com.example.p2k.user.User;
import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Entity
@Table(name="post_tb")
public class Post extends BaseTimeEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "post_id")
    private Long id;

    private String title;

    private String author;

    private String content;

    private Boolean open;

    @Enumerated(EnumType.STRING)
    private Category category;

    @ManyToOne
    @JoinColumn(name = "course_id")
    private Course course;

    @ManyToOne
    @JoinColumn(name = "user_id")
    private User user;

    @Builder
    public Post(Long id, String title, String author, String content, Boolean open, Category category, Course course, User user) {
        this.id = id;
        this.title = title;
        this.author = author;
        this.content = content;
        this.open = open;
        this.category = category;
        this.course = course;
        this.user = user;
    }
}
