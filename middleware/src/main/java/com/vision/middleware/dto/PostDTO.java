package com.vision.middleware.dto;

import lombok.*;

import java.util.Date;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class PostDTO {
    private long postId; // needed if we want to get comments for a post
    private UserDTO postedBy;
    private String title;
    private String text;
    private long likeCount;
    private long dislikeCount;
    private Date datePosted;
    private String mediaFileName; // if not present, then it is implied that it is a text post.
}
