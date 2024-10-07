package com.vision.middleware.repo;

import com.vision.middleware.domain.Reply;
import lombok.RequiredArgsConstructor;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ReplyRepository extends JpaRepository<Reply, Long> {
    List<Reply> findByPostId(long postId);
    List<Reply> findByPostIdAndParentReplyIdIsNull(long postId);
    List<Reply> findByParentReplyId(long parentId);
}
