package com.vision.middleware.repo;

import com.vision.middleware.domain.Reply;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface ReplyRepository extends JpaRepository<Reply, Long> {
    Optional<Reply> findById(Long replyId);
    List<Reply> findByPostId(long postId);
    List<Reply> findByIdAndParentReplyIsNull(long replyId);
    List<Reply> findByParentReply(Reply parent);
}
