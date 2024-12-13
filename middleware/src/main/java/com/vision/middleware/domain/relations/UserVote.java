package com.vision.middleware.domain.relations;

import com.vision.middleware.domain.ApplicationUser;
import com.vision.middleware.domain.baseentities.VotableEntity;
import com.vision.middleware.domain.enums.VotableType;
import jakarta.persistence.*;
import lombok.*;

/**
 * Represents a vote cast by a user on a votable entity.
 */
@Entity
@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@Builder
@Table(name = "user_votes", uniqueConstraints = {
        @UniqueConstraint(columnNames = {"user_id", "votable_id", "votable_type"})
})
public class UserVote {
    /**
     * The unique identifier for the user vote.
     */
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "vote_id")
    private long id;

    /**
     * The user who cast the vote.
     */
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    private ApplicationUser user;

    /**
     * The votable entity on which the vote was cast.
     */
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "votable_id", nullable = false)
    private VotableEntity votable;

    /**
     * The type of the votable entity.
     */
    @Column(name = "votable_type", nullable = false)
    @Enumerated(EnumType.STRING)
    private VotableType votableType;

    /**
     * The type of the vote (LIKE or DISLIKE).
     */
    @Column(name = "vote_type", nullable = false)
    @Enumerated(EnumType.STRING)
    private VoteType voteType;

    /**
     * Represents the possible types of votes.
     */
    public enum VoteType {
        /**
         * A positive vote.
         */
        LIKE,
        /**
         * A negative vote.
         */
        DISLIKE
    }
}
