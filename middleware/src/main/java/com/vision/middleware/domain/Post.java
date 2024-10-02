package com.vision.middleware.domain;

import jakarta.persistence.*;
import lombok.*;

import java.sql.Date;

@AllArgsConstructor
@RequiredArgsConstructor
@Getter
@Setter
@Builder
@Entity(name = "posts")
public class Post {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "post_id")
    private long postId;

    @ManyToOne
    @JoinColumn(name = "user_id", nullable = false) // todo: maybe change this later, if maybe we want posts to exist without an associated user.
    private ApplicationUser postedBy;

    private String title;
    private String text;
    private long likeCount;
    private long dislikeCount;

    private Date datePosted;
}
