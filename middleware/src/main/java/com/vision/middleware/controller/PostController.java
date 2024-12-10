package com.vision.middleware.controller;

import com.vision.middleware.domain.ApplicationUser;
import com.vision.middleware.domain.Post;
import com.vision.middleware.domain.MediaPost;
import com.vision.middleware.domain.relations.UserVote;
import com.vision.middleware.dto.PostDTO;
import com.vision.middleware.dto.UserDTO;
import com.vision.middleware.dto.VoteDTO;
import com.vision.middleware.repo.UserRepository;
import com.vision.middleware.service.PostService;
import com.vision.middleware.utils.JwtUtil;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Date;
import java.util.List;
import java.util.Optional;

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
    private final UserRepository userRepository;

    @PostMapping("/new")
    public ResponseEntity<PostDTO> createPost(@RequestHeader("Authorization") String token, @RequestBody PostDTO postDTO) {
        long id = jwtUtil.checkJwtAuthAndGetUserId(token);
        Post createdPost = postService.createPost(postDTO, id);
        return ResponseEntity.ok(buildDTOFromPost(createdPost));
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

    @GetMapping("/by-id")
    public ResponseEntity<PostDTO> getPostById(@RequestParam(value = "id") long postId) {
        Post post = postService.loadPostById(postId);
        PostDTO postDTO = this.buildDTOFromPost(post);
        return ResponseEntity.ok(postDTO);
    }

    @PostMapping("/vote")
    public VoteDTO voteOnPost(@RequestHeader("Authorization") String token, @RequestBody VoteDTO voteDTO) {
        long userId = jwtUtil.checkJwtAuthAndGetUserId(token);
        postService.userVoteOnPost(voteDTO.getVotableId(), userId, voteDTO.getVoteType());
        return voteDTO;
    }

    @GetMapping("/search")
    public ResponseEntity<List<PostDTO>> searchPosts(@RequestParam String query) {
        // todo: do not allow searches with empty queries?
        if (isQueryInvalid(query)) {
            return ResponseEntity.badRequest().body(null); // do not allow
        }

        // perform search and map to DTOs.
        return ResponseEntity.ok(
                postService.searchPosts(query).stream().map(this::buildDTOFromPost).toList()
        );
    }

    @GetMapping("/search-by-user")
    public ResponseEntity<List<PostDTO>> searchPostsByUser(@RequestParam String query, @RequestParam long userId) {
        ApplicationUser user = userRepository.findById(userId).orElse(null);

        // is query valid? does user exist?
        if (isQueryInvalid(query) || user == null) {
            return ResponseEntity.badRequest().body(null);
        }

        // ok to do search.
        return ResponseEntity.ok(
                postService.searchPostsByUser(query, user).stream().map(this::buildDTOFromPost).toList()
        );
    }

    @GetMapping("/search-by-user-date")
    public ResponseEntity<List<PostDTO>> searchPostsByUserAndDate(
            @RequestParam String query,
            @RequestParam long userId,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) Date startDate,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) Date endDate
    ) {
        ApplicationUser user = userRepository.findById(userId).orElse(null);

        // is query valid? is startdate < enddate?
        if (isQueryInvalid(query) || user == null || (startDate.compareTo(endDate) > 0)) {
            return ResponseEntity.badRequest().body(null);
        }

        // perform search
        return ResponseEntity.ok(
                postService.searchPostsByDateRange(query, user, startDate, endDate)
                        .stream().map(this::buildDTOFromPost).toList()
        );
    }

    private boolean isQueryInvalid(String query) {
        return query == null || query.trim().isEmpty();
    }

    private PostDTO buildDTOFromPost(Post post) {
        PostDTO.PostDTOBuilder builder = PostDTO.builder()
                .postId(post.getId())
                .datePosted(post.getDatePosted())
                .dislikeCount(post.getDislikeCount())
                .likeCount(post.getLikeCount())
                .text(post.getText())
                .title(post.getTitle())
                .postedBy(
                        UserDTO.builder().username(post.getPostedBy().getUsername())
                                .displayName(post.getPostedBy().getDisplayName())
                                .userId(post.getPostedBy().getId())
                                .profilePictureFileName(post.getPostedBy().getProfilePictureFileName())
                                .build()
                );

        if (post instanceof MediaPost mediaPost) {
            builder.mediaFileName(mediaPost.getMediaFileName());
        }

        return builder.build();
    }

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
}
