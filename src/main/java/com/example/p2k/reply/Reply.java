package com.example.p2k.reply;

import com.example.p2k.post.Post;
import com.example.p2k.user.BaseTimeEntity;
import com.example.p2k.user.User;
import jakarta.persistence.*;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
@Entity
@Table(name = "reply_tb")
public class Reply extends BaseTimeEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(length = 256, nullable = false)
    private String content;

    @ManyToOne
    @JoinColumn(name = "user_id")
    private User user;

    @ManyToOne
    @JoinColumn(name = "post_id")
    private Post post;

    @Builder
    public Reply(Long id, String content, User user, Post post) {
        this.id = id;
        this.content = content;
        this.user = user;
        this.post = post;
    }
}
