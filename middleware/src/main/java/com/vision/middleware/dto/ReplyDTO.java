package com.vision.middleware.dto;

import lombok.*;

import java.util.Date;
import java.util.Set;

import com.vision.middleware.domain.ApplicationUser;
import com.vision.middleware.domain.Post;
import com.vision.middleware.domain.Reply;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class ReplyDTO {
    private long replyId;
    private Post post;
    private ApplicationUser author; 
    private Date datePosted;
    private String text;
    private Reply parentReply;
    private Set<Reply> childReplies;
}
