package com.vision.middleware.domain;

import java.sql.Date;

import org.hibernate.annotations.ValueGenerationType;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
@Entity
public class Post {
    @Id
    @GeneratedValue(GenerationType = GenerationType.AUTO)
    @Column(name = "post_id")
    private long postId;

    private String title;
    private String text;
    private long likeCount;
    private long dislikeCount;

    private Date datePosted;
}
