package com.vision.middleware.resource;

import java.net.URI;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.data.domain.Page;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

import com.vision.middleware.domain.User;
import com.vision.middleware.domain.Post;
import com.vision.middleware.domain.Reply;
import com.vision.middleware.repo.UserRepo;
import com.vision.middleware.repo.PostRepo;
import com.vision.middleware.repo.ReplyRepo;

import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/demo-fetch")
@RequiredArgsConstructor
public class DemoController {
    @Autowired
    private UserRepo userRepo;
    @Autowired
    private PostRepo postRepo;
    @Autowired
    private ReplyRepo replyRepo;

    @CrossOrigin(origins = {"http://localhost:3000","https://contacts-5min.onrender.com"})
    @GetMapping("/all")
    public List<User> getAllUsers() {
        return userRepo.findAll();
    }

    @CrossOrigin(origins = {"http://localhost:3000","https://contacts-5min.onrender.com/"})
    @GetMapping("/user/{id}")
    public User getUserById(@PathVariable("id") int userId) {
        return userRepo.findByUserId(userId).orElseThrow(()->new RuntimeException("User not found"));
    }

    @CrossOrigin(origins = {"http://localhost:3000","https://contacts-5min.onrender.com/"})
    @GetMapping("/post/{id}")
    public Post getPostById(@PathVariable("id") int postId) {
        return postRepo.findByPostId(postId).orElseThrow(()->new RuntimeException("Post not found"));
    }

    @CrossOrigin(origins = {"http://localhost:3000","https://contacts-5min.onrender.com/"})
    @GetMapping("/reply/{id}")
    public Reply getReplyById(@PathVariable("id") int replyId) {
        return replyRepo.findByReplyId(replyId).orElseThrow(()->new RuntimeException("Reply not found"));
    }
}
