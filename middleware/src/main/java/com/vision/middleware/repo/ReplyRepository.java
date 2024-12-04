package com.vision.middleware.repo;

import com.vision.middleware.domain.Reply;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface ReplyRepository extends JpaRepository<Reply, Long> {
    @Query("SELECT r FROM Reply r WHERE r.post.id = :postId AND r.parentReply IS NULL " +
            "ORDER BY r.voteScore DESC, r.datePosted ASC")
    List<Reply> findTopLevelRepliesByPostId(@Param("postId") Long postId);
    Optional<Reply> findById(long id);
}
