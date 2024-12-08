package com.vision.middleware.domain.relations;

import com.vision.middleware.domain.ApplicationUser;
import com.vision.middleware.domain.baseentities.VotableEntity;
import com.vision.middleware.domain.enums.VotableType;
import jakarta.persistence.*;
import lombok.*;

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
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "vote_id")
    private long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    private ApplicationUser user;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "votable_id", nullable = false)
    private VotableEntity votable;

    @Column(name = "votable_type", nullable = false)
    @Enumerated(EnumType.STRING)
    private VotableType votableType;

    @Column(name = "vote_type", nullable = false)
    @Enumerated(EnumType.STRING)
    private VoteType voteType;

    public enum VoteType {
        LIKE,
        DISLIKE
    }
}
