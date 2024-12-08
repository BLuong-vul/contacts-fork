package com.vision.middleware.domain.baseentities;

import com.vision.middleware.domain.interfaces.Votable;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.experimental.SuperBuilder;

@Entity
@Getter
@Setter
@NoArgsConstructor
@Inheritance(strategy = InheritanceType.JOINED)
@SuperBuilder
public abstract class VotableEntity implements Votable {
    // there is a separate sequence for ids for each class that extends it.
    // classes that extend votable should specify a @SequenceGenerator for themselves.
    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "id_generator")
    private long id;

    private long likeCount;
    private long dislikeCount;

    // derived fields
    @Column(name = "vote_score")
    private long voteScore;

    @PrePersist
    protected void onPrePersist() {
        updateDerivedFields();
    }

    @PreUpdate
    protected void onPreUpdate() {
        updateDerivedFields();
    }

    private void updateDerivedFields() {
        this.voteScore = this.getLikeCount() - this.getDislikeCount();
    }
}
