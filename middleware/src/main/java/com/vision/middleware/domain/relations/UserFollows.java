package com.vision.middleware.domain.relations;

import com.vision.middleware.domain.ApplicationUser;
import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "user_follows")
@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@Builder
public class UserFollows {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "follow_id")
    private long id;

    @ManyToOne
    @JoinColumn(name = "follower_id")
    private ApplicationUser follower;

    @ManyToOne
    @JoinColumn(name = "followee_id")
    private ApplicationUser followee;
}
