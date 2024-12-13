package com.vision.middleware.domain;

import com.vision.middleware.domain.baseentities.VotableEntity;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.Setter;
import lombok.experimental.SuperBuilder;

import java.util.Date;

/**
 * Represents a post in the system, which is a type of votable entity.
 * Each post is associated with an application user and contains a title and text.
 */
@AllArgsConstructor
@RequiredArgsConstructor
@Getter
@Setter
@SuperBuilder
@Entity(name = "posts")
@Inheritance(strategy = InheritanceType.SINGLE_TABLE)
@DiscriminatorColumn(name = "dtype", discriminatorType = DiscriminatorType.STRING)
@DiscriminatorValue("TEXT")
@SequenceGenerator(name = "id_generator", sequenceName = "post_sequence", allocationSize = 1)
public class Post extends VotableEntity {

    /**
     * The user who posted this post.
     */
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    private ApplicationUser postedBy;

    /**
     * The title of the post.
     */
    private String title;

    /**
     * The text content of the post.
     */
    private String text;

    /**
     * The date when the post was posted.
     */
    private Date datePosted;
}
