package com.example.p2k.post;

import lombok.Getter;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

public class PostResponse {

    @Getter
    public static class FindPostsDTO {

        private final Boolean hasPrevious;
        private final Boolean hasNext;
        private final Boolean isEmpty;
        private final int number;
        private final int totalPages;
        private final int startPage;
        private final int endPage;
        private final List<PostDTO> posts;
        private static final int cnt = 5;

        public FindPostsDTO(Page<Post> posts) {
            this.hasPrevious = posts.hasPrevious();
            this.hasNext = posts.hasNext();
            this.isEmpty = posts.isEmpty();
            this.number = posts.getNumber();
            this.totalPages = posts.getTotalPages();
            this.startPage = getStartPage();
            this.endPage = getEndPage();
            this.posts = posts.getContent().stream().map(PostDTO::new).collect(Collectors.toList());
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
        public class PostDTO{
            private final Long id;
            private final String title;
            private final String author;
            private final String content;
            private final LocalDateTime createdDate;

            public PostDTO(Post post) {
                this.id = post.getId();
                this.title = post.getTitle();
                this.author = post.getAuthor();
                this.content = post.getContent();
                this.createdDate = post.getCreatedDate();
            }
        }
    }

    @Getter
    public static class FindPostByIdDTO{

        private final Long id;
        private final String title;
        private final String author;
        private final String content;
        private final LocalDateTime createdDate;
        private final Long userId;

        public FindPostByIdDTO(Post post) {
            this.id = post.getId();
            this.title = post.getTitle();
            this.author = post.getAuthor();
            this.content = post.getContent();
            this.createdDate = post.getCreatedDate();
            this.userId = post.getUser().getId();
        }
    }
}
