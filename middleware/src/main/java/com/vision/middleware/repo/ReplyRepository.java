package com.vision.middleware.repo;

import com.vision.middleware.domain.Reply;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ReplyRepository extends JpaRepository<Reply, Long> {
    List<Reply> findById(long postId);
    List<Reply> findByIdAndParentReplyIsNull(long replyId);
    List<Reply> findByParentReply(Reply parent);
}
