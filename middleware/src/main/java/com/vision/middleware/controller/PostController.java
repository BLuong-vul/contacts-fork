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

/**
 * RESTful controller for managing posts.
 *
 * This controller provides endpoints for creating, retrieving, and interacting with posts.
 * All endpoints require authentication, with user ID extracted from the provided JWT token.
 */
@RestController
@RequestMapping("/post")
@CrossOrigin("*") // todo: change this later
@RequiredArgsConstructor
public class PostController {

    /**
     * Utility for handling JSON Web Tokens (JWTs).
     */
    @Autowired
    private final JwtUtil jwtUtil;

    /**
     * Service layer for post-related business logic.
     */
    @Autowired
    private final PostService postService;

    /**
     * Repository for user data access.
     */
    @Autowired
    private final UserRepository userRepository;

    /**
     * Creates a new post based on the provided PostDTO.
     *
     * @param token        Authorization token containing the user's ID
     * @param postDTO      Post data to be created
     * @return              Newly created PostDTO with generated ID
     */
    @PostMapping("/new")
    public ResponseEntity<PostDTO> createPost(@RequestHeader("Authorization") String token, @RequestBody PostDTO postDTO) {
        long id = jwtUtil.checkJwtAuthAndGetUserId(token);
        Post createdPost = postService.createPost(postDTO, id);
        return ResponseEntity.ok(buildDTOFromPost(createdPost));
    }

    /**
     * Retrieves a paginated list of posts, sorted by the specified criteria.
     *
     * @param page         Page number (0-indexed)
     * @param size         Number of posts per page
     * @param sortBy       Sort criteria ("date" or "popularity")
     * @param beforeDate   Optional filter: posts before this date
     * @param afterDate    Optional filter: posts after this date
     * @return              Paginated list of PostDTOs
     */
    @GetMapping("/all")
    public ResponseEntity<Page<PostDTO>> getPostPage(
            @RequestParam(value = "page", defaultValue = "0") int page,
            @RequestParam(value = "size", defaultValue = "10") int size,
            @RequestParam(value = "sort-by", defaultValue = "date") String sortBy,
            @RequestParam(value = "before-date", required = false) @DateTimeFormat(pattern = "yyyy-MM-dd") Date beforeDate,
            @RequestParam(value = "after-date", required = false) @DateTimeFormat(pattern = "yyyy-MM-dd") Date afterDate) {
        
        Page<Post> posts = postService.getAllPosts(page, size, sortBy, beforeDate, afterDate);
        Page<PostDTO> postsDTO = posts.map(this::buildDTOFromPost);
        
        return ResponseEntity.ok(postsDTO);
    }

    /**
     * Retrieves a paginated list of posts from a specific user, sorted by the specified criteria.
     *
     * @param username     Username of the post author
     * @param page         Page number (0-indexed)
     * @param size         Number of posts per page
     * @param sortBy       Sort criteria ("date" or "popularity")
     * @param beforeDate   Optional filter: posts before this date
     * @param afterDate    Optional filter: posts after this date
     * @return              Paginated list of PostDTOs
     */
    @GetMapping("/by-user")
    public ResponseEntity<Page<PostDTO>> getPostPageByUsername(@RequestParam(value = "username", defaultValue = "") String username,
                           @RequestParam(value = "page", defaultValue = "0") int page,
                           @RequestParam(value = "size", defaultValue = "10") int size,
                           @RequestParam(value = "sort-by", defaultValue = "date") String sortBy,
                           @RequestParam(value = "before-date", required = false) @DateTimeFormat(pattern = "yyyy-MM-dd") Date beforeDate,
                           @RequestParam(value = "after-date", required = false) @DateTimeFormat(pattern = "yyyy-MM-dd") Date afterDate) {
        Page<Post> posts = postService.getAllPostsByUsername(username, page, size, sortBy, beforeDate, afterDate);
        Page<PostDTO> postsDTO = posts.map(this::buildDTOFromPost);
        return ResponseEntity.ok(postsDTO);
    }

    /**
     * Retrieves a post by its ID.
     *
     * @param postId       ID of the post to retrieve
     * @return              PostDTO if found, or 500
     */
    @GetMapping("/by-id")
    public ResponseEntity<PostDTO> getPostById(@RequestParam(value = "id") long postId) {
        Post post = postService.loadPostById(postId);
        PostDTO postDTO = this.buildDTOFromPost(post);
        return ResponseEntity.ok(postDTO);
    }

    /**
     * Casts a vote on a post.
     *
     * @param token        Authorization token containing the user's ID
     * @param voteDTO      Vote data (post ID and vote type)
     * @return              Updated VoteDTO
     */
    @PostMapping("/vote")
    public VoteDTO voteOnPost(@RequestHeader("Authorization") String token, @RequestBody VoteDTO voteDTO) {
        long userId = jwtUtil.checkJwtAuthAndGetUserId(token);
        postService.userVoteOnPost(voteDTO.getVotableId(), userId, voteDTO.getVoteType());
        return voteDTO;
    }

    /**
     * Removes the user's vote from a post.
     *
     * @param token        Authorization token containing the user's ID
     * @param votableId    ID of the post to unvote
     * @return              204 No Content
     */
    @DeleteMapping("/unvote")
    public ResponseEntity<Void> unvoteOnPost(@RequestHeader("Authorization") String token, @RequestParam long votableId) {
        long userId = jwtUtil.checkJwtAuthAndGetUserId(token);
        postService.removeUserVote(votableId, userId);
        return ResponseEntity.noContent().build();
    }

    /**
     * Retrieves the user's current vote on a post, if any.
     *
     * @param token        Authorization token containing the user's ID
     * @param votableId    ID of the post to check
     * @return              VoteType if present, or 204 No Content
     */
    @GetMapping("/get-vote")
    public ResponseEntity<UserVote.VoteType> checkUserVote(@RequestHeader("Authorization") String token, @RequestParam long votableId) {
        long userId = jwtUtil.checkJwtAuthAndGetUserId(token);
        Optional<UserVote.VoteType> voteType = postService.getUserVote(votableId, userId);

        return voteType
            .map(ResponseEntity::ok) // Return the vote type if present
            .orElse(ResponseEntity.noContent().build()); // Null if no vote exists
    }

    /**
     * Searches for posts containing the specified query.
     *
     * @param query        Search query
     * @return              List of matching PostDTOs
     */
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

    /**
     * Searches for posts containing the specified query, authored by a specific user.
     *
     * @param query        Search query
     * @param userId       ID of the post author
     * @return              List of matching PostDTOs
     */
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

    /**
     * Searches for posts containing the specified query, authored by a specific user, within a date range.
     *
     * @param query        Search query
     * @param userId       ID of the post author
     * @param startDate    Start of the date range (inclusive)
     * @param endDate      End of the date range (inclusive)
     * @return              List of matching PostDTOs
     */
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

    /**
     * Checks if a search query is invalid (null or empty).
     *
     * @param query        Search query to validate
     * @return              True if the query is invalid, false otherwise
     */
    private boolean isQueryInvalid(String query) {
        return query == null || query.trim().isEmpty();
    }

    /**
     * Builds a PostDTO from a Post entity, mapping relevant fields for external exposure.
     *
     * If the Post is an instance of MediaPost, the media file name is also included in the DTO.
     *
     * @param post the Post entity to be converted
     * @return a PostDTO representation of the input Post
     */
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
}
