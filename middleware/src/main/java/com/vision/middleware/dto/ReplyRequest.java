package com.vision.middleware.dto;

import lombok.Builder;
import lombok.Data;

/**
 * Represents a request to create a reply.
 * A reply can be made to a post or another reply.
 */
@Data
@Builder
public class ReplyRequest {
    private long postId;
    private long toReplyId; // default 0, which means that it is to the post itself.
    private String text;
}
