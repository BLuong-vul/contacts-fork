package com.vision.middleware.repo;

import com.vision.middleware.domain.ApplicationUser;
import com.vision.middleware.domain.Post;
import com.vision.middleware.domain.relations.UserVote;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface VoteRepository extends JpaRepository<UserVote, Long> {
    Optional<UserVote> findById(long id);
    List<UserVote> findByUser(ApplicationUser user);
    List<UserVote> findByPost(Post post);
}
