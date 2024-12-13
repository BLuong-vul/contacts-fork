package com.vision.middleware.repo;

import com.vision.middleware.domain.Reply;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

/**
 * Repository for managing database operations related to Replies.
 */
@Repository
public interface ReplyRepository extends JpaRepository<Reply, Long> {

    /**
     * Retrieves top-level replies (i.e., those without a parent reply) for a given post,
     * ordered by their vote score in descending order and then by their post date in ascending order.
     *
     * @param postId the ID of the post for which to retrieve top-level replies
     * @return a list of top-level replies for the specified post
     */
    @Query("SELECT r FROM Reply r WHERE r.post.id = :postId AND r.parentReply IS NULL " +
            "ORDER BY r.voteScore DESC, r.datePosted ASC")
    List<Reply> findTopLevelRepliesByPostId(@Param("postId") Long postId);

    /**
     * Attempts to retrieve a Reply by its ID.
     *
     * @param id the ID of the Reply to retrieve
     * @return an Optional containing the Reply if found, or an empty Optional if not found
     * @note This method's implementation is inherited from JpaRepository and is included here for completeness.
     *       Its behavior is equivalent to calling {@link JpaRepository#findById(Object)}.
     */
    Optional<Reply> findById(long id);
}
