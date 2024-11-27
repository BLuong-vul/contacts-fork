package com.vision.middleware.dto;

import lombok.*;

import java.util.Date;
import java.util.Set;
import java.util.HashSet;

import com.vision.middleware.domain.ApplicationUser;
import com.vision.middleware.domain.Post;
import com.vision.middleware.domain.Reply;

import com.vision.middleware.dto.UserDTO;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class ReplyDTO {
    private long replyId;
    private long postId;
    private UserDTO author; 
    private Date datePosted;
    private String text;
    private Long parentReplyId; // Nullable
    private Set<ReplyDTO> childReplies = new HashSet<>();
}
