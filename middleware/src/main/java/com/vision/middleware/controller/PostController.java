package com.vision.middleware.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.data.domain.Page;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.time.LocalDateTime;
import java.sql.Date;

import com.vision.middleware.domain.Post;
import com.vision.middleware.repo.PostRepository;


@RestController
@RequestMapping("/post")
@CrossOrigin("*") // todo: change this later
public class PostController {
	@Autowired
	private PostRepository postRepo;

    @GetMapping("/id/{id}")
    public Post getPostById(@PathVariable("id") long postId) {
        return postRepo.findByPostId(postId).orElseThrow(()->new RuntimeException("Post not found"));
    }

    @GetMapping("/before/{date}")
    public List<Post> getPostsBeforeDate(@PathVariable("date") String dateStr) {
        // LocalDateTime date = LocalDateTime.parse(dateStr);
        return postRepo.findAllByDatePostedBefore(Date.valueOf(dateStr));
    }
}
