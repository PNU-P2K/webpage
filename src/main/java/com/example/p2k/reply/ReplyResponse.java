package com.example.p2k.reply;

import lombok.Getter;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

public class ReplyResponse {

    @Getter
    public static class FindRepliesDTO {

        private final List<ReplyDTO> replies;

        public FindRepliesDTO(List<Reply> replies) {
            this.replies = replies.stream().map(ReplyDTO::new).collect(Collectors.toList());
        }

        @Getter
        public class ReplyDTO {
            private final Long id;
            private final String content;
            private final LocalDateTime createdDate;
            private final String userName;

            public ReplyDTO(Reply reply) {
                this.id = reply.getId();
                this.content = reply.getContent();
                this.createdDate = reply.getCreatedDate();
                this.userName = reply.getUser().getName();
            }
        }
    }
}
