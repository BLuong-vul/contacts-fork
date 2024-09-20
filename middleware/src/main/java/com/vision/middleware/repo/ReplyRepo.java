package com.vision.middleware.repo;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

import com.vision.middleware.domain.Reply;

@Repository
public interface ReplyRepo extends JpaRepository<Reply, Integer> {
	Optional<Reply> findByReplyId(int replyId);
}