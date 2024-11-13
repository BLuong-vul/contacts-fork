package com.vision.middleware.controller;

import com.vision.middleware.domain.Post;
import com.vision.middleware.dto.PostDTO;
import com.vision.middleware.dto.UserDTO;
import com.vision.middleware.dto.VoteDTO;
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

    @PostMapping("/new")
    public Post createPost(@RequestHeader("Authorization") String token, @RequestBody PostDTO postDTO) {
        long id = jwtUtil.checkJwtAuthAndGetUserId(token);
        return postService.createPost(postDTO, id);
    }

    @GetMapping("/all")
    public ResponseEntity<Page<PostDTO>> getPostPage(@RequestParam(value = "page", defaultValue = "0") int page,
                                                            @RequestParam(value = "size", defaultValue = "10") int size) {
        Page<Post> posts = postService.getAllPosts(page, size);

        Page<PostDTO> postsDTO = posts.map(
                post -> PostDTO.builder()
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

    @PostMapping("/vote")
    public VoteDTO voteOnPost(@RequestHeader("Authorization") String token, @RequestBody VoteDTO voteDTO) {
        long userId = jwtUtil.checkJwtAuthAndGetUserId(token);
        postService.userVoteOnPost(voteDTO.getVotableId(), userId, voteDTO.getVoteType());
        return voteDTO;
    }
}
