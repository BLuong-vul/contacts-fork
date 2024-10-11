package com.vision.middleware.dto;

import lombok.*;

import java.util.Date;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class PostDTO {
    // note: we shouldn't need any more information than this: will be figured out when making the post.
    private UserDTO postedBy;
    private String title;
    private String text;
    private long likeCount;
    private long dislikeCount;
    private Date datePosted;
}
