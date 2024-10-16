package com.vision.middleware.domain.relations;

import com.vision.middleware.domain.ApplicationUser;
import com.vision.middleware.domain.Post;
import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "user_posted")
@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@Builder
public class UserPosted {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "user_post_id")
    private long id;

    @ManyToOne
    @JoinColumn(name = "posted_by")
    private ApplicationUser postedBy;

    @ManyToOne
    @JoinColumn(name = "post_id")
    private Post post;
}
