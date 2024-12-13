package com.vision.middleware.domain.baseentities;

import com.vision.middleware.domain.interfaces.Votable;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.experimental.SuperBuilder;

/**
 * Abstract entity class representing a votable item.
 * All entities that extend this class can be voted on, with like and dislike counts.
 */
@Entity
@Getter
@Setter
@NoArgsConstructor
@Inheritance(strategy = InheritanceType.JOINED)
@SuperBuilder
public abstract class VotableEntity implements Votable {

    /**
     * Unique identifier for the votable entity.
     * Each subclass should specify its own @SequenceGenerator.
     */
    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "id_generator")
    private long id;

    /**
     * Count of likes for the votable entity.
     */
    private long likeCount;

    /**
     * Count of dislikes for the votable entity.
     */
    private long dislikeCount;

    /**
     * Derived field representing the vote score.
     * Calculated as the difference between likeCount and dislikeCount.
     */
    @Column(name = "vote_score")
    private long voteScore;

    /**
     * Callback method invoked before the entity is persisted.
     * Updates derived fields before persisting.
     */
    @PrePersist
    protected void onPrePersist() {
        updateDerivedFields();
    }

    /**
     * Callback method invoked before the entity is updated.
     * Updates derived fields before updating.
     */
    @PreUpdate
    protected void onPreUpdate() {
        updateDerivedFields();
    }

    /**
     * Updates the derived fields of the entity.
     * Specifically, calculates and sets the voteScore.
     */
    private void updateDerivedFields() {
        this.voteScore = this.getLikeCount() - this.getDislikeCount();
    }
}
