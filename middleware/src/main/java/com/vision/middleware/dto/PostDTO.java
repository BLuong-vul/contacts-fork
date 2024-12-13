package com.vision.middleware.dto;

import lombok.*;

import java.util.Date;

/**
 * Data Transfer Object representing a post.
 * This class is used to transfer post data between layers of the application.
 * It includes details such as the post ID, user who posted, title, text content,
 * like and dislike counts, date of posting, and media file name.
 */
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
