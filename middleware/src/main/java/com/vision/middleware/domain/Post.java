package com.vision.middleware.domain;

import com.vision.middleware.domain.baseEntities.VotableEntity;
import com.vision.middleware.domain.interfaces.Votable;
import jakarta.persistence.*;
import lombok.*;
import lombok.experimental.SuperBuilder;

import java.util.Date;

@AllArgsConstructor
@RequiredArgsConstructor
@Getter
@Setter
@SuperBuilder
@Entity(name = "posts")
@Inheritance(strategy = InheritanceType.SINGLE_TABLE)
@DiscriminatorColumn(name = "dtype", discriminatorType = DiscriminatorType.STRING)
@DiscriminatorValue("TEXT")
public class Post extends VotableEntity {
    @ManyToOne(fetch = FetchType.LAZY, cascade = CascadeType.REMOVE)
    @JoinColumn(name = "user_id", nullable = false)
    private ApplicationUser postedBy;

    private String title;
    private String text;

    private Date datePosted;
}
