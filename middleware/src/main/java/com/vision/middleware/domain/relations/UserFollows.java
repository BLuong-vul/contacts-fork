package com.vision.middleware.domain.relations;

import com.vision.middleware.domain.ApplicationUser;
import jakarta.persistence.*;
import lombok.*;

/**
 * Represents a relationship where one user follows another user.
 */
@Entity
@Table(name = "user_follows")
@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@Builder
public class UserFollows {
    /**
     * Unique identifier for the user follow relationship.
     */
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "follow_id")
    private long id;

    /**
     * The user who is following another user.
     */
    @ManyToOne
    @JoinColumn(name = "follower_id")
    private ApplicationUser follower;

    /**
     * The user who is being followed.
     */
    @ManyToOne
    @JoinColumn(name = "followee_id")
    private ApplicationUser followee;
}
