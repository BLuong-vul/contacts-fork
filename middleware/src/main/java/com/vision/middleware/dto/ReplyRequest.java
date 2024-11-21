package com.vision.middleware.dto;

import lombok.Builder;
import lombok.Data;
import lombok.Getter;
import lombok.Setter;

@Data
@Builder
@Getter
@Setter
public class ReplyRequest {
    private long postId;
    private long toReplyId; // default 0, which means that it is to the post itself.
    private String text;
}
