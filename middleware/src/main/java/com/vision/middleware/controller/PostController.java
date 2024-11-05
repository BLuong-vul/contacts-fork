package com.vision.middleware.controller;

import com.vision.middleware.domain.Post;
import com.vision.middleware.dto.PostDTO;
import com.vision.middleware.dto.UserDTO;
import com.vision.middleware.service.PostService;
import com.vision.middleware.utils.JwtUtil;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.http.ResponseEntity;
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

    // This used to return a Post and has been changed to just return an OK response entity. Oct 31 
    @PostMapping("/new")
    public ResponseEntity<Void> createPost(@RequestHeader("Authorization") String token, @RequestBody PostDTO postDTO) {
        long id = jwtUtil.checkJwtAuthAndGetUserId(token);
        Post createdPost = postService.createPost(postDTO, id);
        return ResponseEntity.ok().build();
    }

    @GetMapping("/all")
    public ResponseEntity<Page<PostDTO>> getPostPage(@RequestParam(value = "page", defaultValue = "0") int page,
                                                            @RequestParam(value = "size", defaultValue = "10") int size) {
        Page<Post> posts = postService.getAllPosts(page, size);
        
        Page<PostDTO> postsDTO = posts.map(
                /*Design Pattern: Builder*/
                post -> PostDTO.builder()
                        .postId(post.getPostId())
                        .datePosted(post.getDatePosted())
                        .dislikeCount(post.getDislikeCount())
                        .text(post.getText())
                        .title(post.getTitle())
                        .postedBy(
                                UserDTO.builder().username(post.getPostedBy().getUsername())
                                        .userId(post.getPostedBy().getId())
                                        .build()
                        )
                        .build()
                /*Design Pattern: Builder*/
        );

        return ResponseEntity.ok(postsDTO);
    }

    @GetMapping("/by-user")
    public ResponseEntity<Page<PostDTO>> getPostPageByUsername(@RequestParam(value = "username", defaultValue = "") String username, 
                                                               @RequestParam(value = "page", defaultValue = "0") int page, 
                                                               @RequestParam(value = "size", defaultValue = "10") int size) {
        Page<Post> posts = postService.getAllPostsByUsername(username, page, size);

        Page<PostDTO> postsDTO = posts.map(
                post -> PostDTO.builder()
                        .postId(post.getPostId())
                        .datePosted(post.getDatePosted())
                        .dislikeCount(post.getDislikeCount())
                        .text(post.getText())
                        .title(post.getTitle())
                        .postedBy(
                                UserDTO.builder().username(post.getPostedBy().getUsername())
                                        .userId(post.getPostedBy().getId())
                                        .build()
                        )
                        .build()
        );

        return ResponseEntity.ok(postsDTO);
    }
}
