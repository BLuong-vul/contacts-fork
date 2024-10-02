package com.vision.middleware.repo;

import com.vision.middleware.domain.Post;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;
import java.util.List;
import java.sql.Date;

@Repository
public interface PostRepository extends JpaRepository<Post, Long> {
    Optional<Post> findByPostId(long id);
    List<Post> findAllByDatePostedBefore(Date date);
}
