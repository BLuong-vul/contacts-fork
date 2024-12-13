package com.vision.middleware.domain.relations;

import com.vision.middleware.domain.ApplicationUser;
import com.vision.middleware.domain.Post;
import jakarta.persistence.*;
import lombok.*;

/**
 * Represents the relationship between a user and a post, indicating that the user has posted the post.
 */
@Entity
@Table(name = "user_posted")
@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@Builder
public class UserPosted {
    /**
     * Unique identifier for the user-post relationship.
     */
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "user_post_id")
    private long id;

    /**
     * The user who posted the post.
     */
    @ManyToOne
    @JoinColumn(name = "posted_by")
    private ApplicationUser postedBy;

    /**
     * The post that was posted by the user.
     */
    @ManyToOne
    @JoinColumn(name = "post_id")
    private Post post;
}
