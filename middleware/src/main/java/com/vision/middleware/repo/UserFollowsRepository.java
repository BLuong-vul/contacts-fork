package com.vision.middleware.repo;

import com.vision.middleware.domain.ApplicationUser;
import com.vision.middleware.domain.relations.UserFollows;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface UserFollowsRepository extends JpaRepository<UserFollows, Long> {
    List<UserFollows> findByFollower(ApplicationUser follower);
    List<UserFollows> findByFollowee(ApplicationUser followee);

    void deleteByFollowerAndFollowee(ApplicationUser follower, ApplicationUser followee);
}
