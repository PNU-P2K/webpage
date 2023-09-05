package com.example.p2k.reply;

import com.example.p2k.post.Post;
import com.example.p2k.post.PostResponse;
import lombok.Getter;
import org.springframework.data.domain.Page;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

public class ReplyResponse {

    @Getter
    public static class FindRepliesDTO {

        private final Boolean hasPrevious;
        private final Boolean hasNext;
        private final Boolean isEmpty;
        private final int number;
        private final int totalPages;
        private final int startPage;
        private final int endPage;
        private final List<ReplyDTO> replies;
        private static final int cnt = 5;

        public FindRepliesDTO(Page<Reply> replies) {
            this.hasPrevious = replies.hasPrevious();
            this.hasNext = replies.hasNext();
            this.isEmpty = replies.isEmpty();
            this.number = replies.getNumber();
            this.totalPages = replies.getTotalPages();
            this.startPage = getStartPage();
            this.endPage = getEndPage();
            this.replies = replies.getContent().stream().map(ReplyDTO::new).collect(Collectors.toList());
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
        public class ReplyDTO {
            private final Long id;
            private final String content;
            private final String author;
            private final LocalDateTime createdDate;
            private final Long step;
            private final Long userId;

            public ReplyDTO(Reply reply) {
                this.id = reply.getId();
                this.content = reply.getContent();
                this.author = reply.getAuthor();
                this.createdDate = reply.getCreatedDate();
                this.step = reply.getStep();
                this.userId = reply.getUser().getId();
            }
        }
    }
}
