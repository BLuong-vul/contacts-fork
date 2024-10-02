package com.vision.middleware.controller;

import com.vision.middleware.domain.Post;
import com.vision.middleware.dto.PostDTO;
import com.vision.middleware.repo.PostRepository;
import com.vision.middleware.service.PostService;
import com.vision.middleware.utils.JwtUtil;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/post")
@CrossOrigin("*") // todo: change this later
@RequiredArgsConstructor
public class PostController {

    @Autowired
    private final JwtUtil jwtUtil;

    @Autowired
    private final PostService postService;

    @PostMapping("/new")
    public Post newPost(@RequestHeader String token, PostDTO postDTO) {

        if (token != null && token.startsWith("Bearer ")) {
            token = token.substring(7);
            String username = jwtUtil.extractUsername(token);

            if (username != null && jwtUtil.isTokenValid(token, username)) {
                // actions to be performed
            }
        }

        return null;
    }
}
