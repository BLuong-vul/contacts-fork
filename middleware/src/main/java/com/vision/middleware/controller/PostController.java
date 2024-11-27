package com.vision.middleware.controller;

import com.vision.middleware.domain.Post;
import com.vision.middleware.domain.Reply;
import com.vision.middleware.domain.relations.UserVote;
import com.vision.middleware.domain.relations.UserVote;
import com.vision.middleware.dto.PostDTO;
import com.vision.middleware.dto.UserDTO;
import com.vision.middleware.dto.VoteDTO;
import com.vision.middleware.dto.ReplyDTO;
import com.vision.middleware.service.PostService;
import com.vision.middleware.service.ReplyService;
import com.vision.middleware.utils.JwtUtil;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Optional;
import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/post")
@CrossOrigin("*") // todo: change this later
@RequiredArgsConstructor
public class PostController {

    @Autowired
    private final JwtUtil jwtUtil;

    @Autowired
    private final PostService postService;

    @Autowired
    private final ReplyService replyService;

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
        Page<PostDTO> postsDTO = posts.map(this::buildDTOFromPost);
        return ResponseEntity.ok(postsDTO);
    }

    @GetMapping("/by-user")
    public ResponseEntity<Page<PostDTO>> getPostPageByUsername(@RequestParam(value = "username", defaultValue = "") String username,
                                                               @RequestParam(value = "page", defaultValue = "0") int page,
                                                               @RequestParam(value = "size", defaultValue = "10") int size) {
        Page<Post> posts = postService.getAllPostsByUsername(username, page, size);
        Page<PostDTO> postsDTO = posts.map(this::buildDTOFromPost);
        return ResponseEntity.ok(postsDTO);
    }

    @PostMapping("/vote")
    public VoteDTO voteOnPost(@RequestHeader("Authorization") String token, @RequestBody VoteDTO voteDTO) {
        long userId = jwtUtil.checkJwtAuthAndGetUserId(token);
        postService.userVoteOnPost(voteDTO.getVotableId(), userId, voteDTO.getVoteType());
        return voteDTO;
    }

    private PostDTO buildDTOFromPost(Post post) {
        // only the information required.
        return PostDTO.builder()
                .postId(post.getId())
                .datePosted(post.getDatePosted())
                .dislikeCount(post.getDislikeCount())
                .likeCount(post.getLikeCount())
                .text(post.getText())
                .title(post.getTitle())
                .postedBy(
                        UserDTO.builder().username(post.getPostedBy().getUsername())
                                .userId(post.getPostedBy().getId())
                                .build()
                )
    @DeleteMapping("/unvote")
    public ResponseEntity<Void> unvoteOnPost(@RequestHeader("Authorization") String token, @RequestParam long votableId) {
        long userId = jwtUtil.checkJwtAuthAndGetUserId(token);
        postService.removeUserVote(votableId, userId);
        return ResponseEntity.noContent().build(); 
    }

    @GetMapping("/get-vote")
    public ResponseEntity<UserVote.VoteType> checkUserVote(@RequestHeader("Authorization") String token, @RequestParam long votableId) {
        long userId = jwtUtil.checkJwtAuthAndGetUserId(token);
        Optional<UserVote.VoteType> voteType = postService.getUserVote(votableId, userId);

        return voteType
            .map(ResponseEntity::ok) // Return the vote type if present
            .orElse(ResponseEntity.noContent().build()); // Null if no vote exists
    }

    @PostMapping("/create-reply")
    public ResponseEntity<Reply> createReply(@RequestHeader("Authorization") String token, @RequestBody ReplyDTO replyDTO){
        long userId = jwtUtil.checkJwtAuthAndGetUserId(token);
        Reply createdReply = replyService.createReply(replyDTO, replyDTO.getPostId(), userId);
        return ResponseEntity.ok(createdReply);
    }

    @GetMapping("/get-replies")
    public ResponseEntity<List<ReplyDTO>> getRepliesByPostId(@RequestParam long postId) {
        List<Reply> replies = replyService.getReplyTree(postId);

        List<ReplyDTO> replyDTO = replies.stream().map(this::mapToReplyDTO).collect(Collectors.toList());
        return ResponseEntity.ok(replyDTO);
    }

    private ReplyDTO mapToReplyDTO(Reply reply) {
        UserDTO authorDTO = UserDTO.builder()
                .userId(reply.getAuthor().getId())
                .username(reply.getAuthor().getUsername())
                .displayName(reply.getAuthor().getDisplayName())
                .build();

        return ReplyDTO.builder()
                .replyId(reply.getId())
                .postId(reply.getPost().getId())
                .author(authorDTO)
                .datePosted(reply.getDatePosted())
                .text(reply.getText())
                .parentReplyId(reply.getParentReply() != null ? reply.getParentReply().getId() : null)
                .childReplies(reply.getChildReplies().stream()
                        .map(this::mapToReplyDTO)
                        .collect(Collectors.toSet()))
                .build();
    }
}
