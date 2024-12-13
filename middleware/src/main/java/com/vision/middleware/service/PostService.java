package com.vision.middleware.service;

import com.vision.middleware.domain.ApplicationUser;
import com.vision.middleware.domain.MediaPost;
import com.vision.middleware.domain.Post;
import com.vision.middleware.domain.relations.UserVote;
import com.vision.middleware.dto.PostDTO;
import com.vision.middleware.exceptions.IdNotFoundException;
import com.vision.middleware.repo.PostRepository;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import java.util.Date;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.Optional;

import java.time.LocalDate;

/**
 * Service class responsible for handling post-related operations.
 */
@Service
@RequiredArgsConstructor
@Transactional(rollbackOn = Exception.class) // <- each method call is treated as a single transaction.
public class PostService {

    /**
     * Repository for post data access.
     */
    @Autowired
    private final PostRepository postRepository;

    /**
     * Service for user-related operations.
     */
    @Autowired
    private final UserService userService;

    /**
     * Service for voting operations.
     */
    @Autowired
    private final VotingService votingService;

    /**
     * Creates a new post based on the provided PostDTO and user ID.
     *
     * @param postDTO   Data transfer object containing post information.
     * @param userId    ID of the user creating the post.
     * @return          The newly created Post entity.
     */
    public Post createPost(PostDTO postDTO, long userId) {

        Date date = new Date();
        ApplicationUser postingUser = userService.loadUserById(userId);
        Post newPost;

        if (postDTO.getMediaFileName() == null) {
            // text post
            newPost = Post.builder()
                    .postedBy(postingUser)
                    .datePosted(date)
                    .title(postDTO.getTitle())
                    .text(postDTO.getText())
                    .build();
        } else {
            // image post
            // todo: check if media exists in the s3 bucket
            newPost = MediaPost.builder()
                    .postedBy(postingUser)
                    .datePosted(date)
                    .title(postDTO.getTitle())
                    .text(postDTO.getText())
                    .mediaFileName(postDTO.getMediaFileName())
                    .build();
        }
        return postRepository.save(newPost);
    }

    /**
     * Allows a user to vote on a post.
     *
     * @param postId   ID of the post being voted on.
     * @param userId   ID of the user casting the vote.
     * @param voteType Type of vote (e.g., like, dislike).
     */
    public void userVoteOnPost(long postId, long userId, UserVote.VoteType voteType) {
        Post post = postRepository.findById(postId).orElseThrow(
                () -> new IdNotFoundException("Post id " + postId + " not found.")
        );
        ApplicationUser user = userService.loadUserById(userId);

        votingService.voteOnVotable(user, post, voteType);
    }

    /**
     * Removes a user's vote from a post.
     *
     * @param postId   ID of the post from which the vote is being removed.
     * @param userId   ID of the user whose vote is being removed.
     */
    public void removeUserVote(long postId, long userId) {
        Post post = postRepository.findById(postId).orElseThrow(
                () -> new IdNotFoundException("Post id " + postId + " not found.")
        );
        ApplicationUser user = userService.loadUserById(userId);

        votingService.deleteVote(user, post);
    }

    /**
     * Retrieves the type of vote (if any) a user has cast on a post.
     *
     * @param postId   ID of the post.
     * @param userId   ID of the user.
     * @return          Optional containing the vote type (like/dislike) or an empty Optional if no vote exists.
     */
    public Optional<UserVote.VoteType> getUserVote(long postId, long userId){
        Post post = postRepository.findById(postId).orElseThrow(
            () -> new IdNotFoundException("Post id " + postId + " not found.")
        );
        ApplicationUser user = userService.loadUserById(userId);

        return votingService.getUserVoteOnVotable(user, post);
    }

    /**
     * Retrieves a paginated list of all posts, sorted by the specified criteria.
     *
     * @param page       Page number (0-indexed).
     * @param size       Number of posts per page.
     * @param sortBy     Sorting criteria ("new" or "popularity").
     * @param beforeDate Date before which posts should be retrieved (inclusive).
     * @param afterDate  Date after which posts should be retrieved (inclusive).
     * @return          Paginated list of Post entities.
     */
    public Page<Post> getAllPosts(int page, int size, String sortBy, Date beforeDate, Date afterDate) {
        Pageable pageable;
        if ("popularity".equals(sortBy)) {
            pageable = PageRequest.of(page, size, Sort.by(Sort.Order.desc("likeCount"), Sort.Order.desc("datePosted")));
        } else {
            pageable = PageRequest.of(page, size, Sort.by(Sort.Order.desc("datePosted")));
        }

        return postRepository.findAllPostsWithFilters(pageable, beforeDate, afterDate);
    }

    /**
     * Retrieves a paginated list of posts from a specific user, sorted by the specified criteria.
     *
     * @param username   Username of the post's author.
     * @param page       Page number (0-indexed).
     * @param size       Number of posts per page.
     * @param sortBy     Sorting criteria ("new" or "popularity").
     * @param beforeDate Date before which posts should be retrieved (inclusive).
     * @param afterDate  Date after which posts should be retrieved (inclusive).
     * @return          Paginated list of Post entities.
     */
    public Page<Post> getAllPostsByUsername(String username, int page, int size, String sortBy, Date beforeDate, Date afterDate) {
        ApplicationUser user = userService.loadUserByUsername(username); 

        Pageable pageable;
        if ("popularity".equals(sortBy)) {
            pageable = PageRequest.of(page, size, Sort.by(Sort.Order.desc("likeCount"), Sort.Order.desc("datePosted")));
        } else {
            pageable = PageRequest.of(page, size, Sort.by(Sort.Order.desc("datePosted")));
        }
        
        return postRepository.findAllPostsByUserWithFilters(user, pageable, beforeDate, afterDate);
    }

    /**
     * Retrieves a post by its ID.
     *
     * @param postId ID of the post to retrieve.
     * @return       The Post entity, or throws an exception if not found.
     */
    public Post loadPostById(long postId) {
        return postRepository.findById(postId)
                .orElseThrow(() -> new NoSuchElementException("Post with ID " + postId + " not found."));
    }

    /**
     * Searches for posts containing the specified query string (case-insensitive).
     *
     * @param query Search query string.
     * @return       List of matching Post entities.
     */
    public List<Post> searchPosts(String query) {
        return postRepository.searchPosts(query, null);
    }

    /**
     * Searches for posts containing the specified query string (case-insensitive),
     * restricted to a specific user's posts.
     *
     * @param query  Search query string.
     * @param user   User whose posts are being searched.
     * @return       List of matching Post entities.
     */
    public List<Post> searchPostsByUser(String query, ApplicationUser user) {
        return postRepository.searchPosts(query, user);
    }

    public List<Post> searchPostsByDateRange(
            String query,
            ApplicationUser user,
            Date startDate,
            Date endDate
    ) {
        return postRepository.searchPostsByDateAndQuery(query, user, startDate, endDate);
    }
}
