package com.vision.middleware.domain;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.Setter;

import java.sql.Date;

@AllArgsConstructor
@RequiredArgsConstructor
@Getter
@Setter
@MappedSuperclass
public class Post {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "post_id")
    private final long postId;

    @OneToOne(mappedBy = "userId")
    private ApplicationUser poster;

    private String title;
    private String text;
    private long likeCount;
    private long dislikeCount;

    private Date datePosted;
}
