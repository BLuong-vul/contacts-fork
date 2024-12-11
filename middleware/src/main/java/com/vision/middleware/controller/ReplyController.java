package com.vision.middleware.controller;

import com.vision.middleware.domain.ApplicationUser;
import com.vision.middleware.domain.Post;
import com.vision.middleware.domain.Reply;
import com.vision.middleware.domain.relations.UserVote;
import com.vision.middleware.dto.ReplyDTO;
import com.vision.middleware.dto.ReplyRequest;
import com.vision.middleware.dto.UserDTO;
import com.vision.middleware.dto.VoteDTO;
import com.vision.middleware.service.PostService;
import com.vision.middleware.service.ReplyService;
import com.vision.middleware.service.UserService;
import com.vision.middleware.service.VotingService;
import com.vision.middleware.utils.JwtUtil;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;

/**
 * RESTful API controller for managing replies to posts.
 *
 * This controller handles requests related to replies, including creating new replies,
 * voting on existing replies, and retrieving reply information.
 *
 * All endpoints require authentication, with the exception of
 * {@link #getRepliesToPostNoAuth(long) retrieving replies to a post}.
 */
@RestController
@RequestMapping("/replies")
@RequiredArgsConstructor
@CrossOrigin("*")
public class ReplyController {

    /**
     * Service for managing replies.
     */
    @Autowired
    private final ReplyService replyService;

    /**
     * Service for managing users.
     */
    @Autowired
    private final UserService userService;

    /**
     * Service for managing posts.
     */
    @Autowired
    private final PostService postService;

    /**
     * Service for managing votes.
     */
    @Autowired
    private final VotingService votingService;

    /**
     * Utility for JWT authentication.
     */
    @Autowired
    private final JwtUtil jwtUtil;

    /**
     * Retrieves the comment tree for a post without requiring authentication.
     *
     * @param postId the ID of the post for which to retrieve replies
     * @return a list of ReplyDTOs representing the comment tree for the post
     */
    @GetMapping("/post/{postId}")
    public ResponseEntity<List<ReplyDTO>> getRepliesToPostNoAuth(@PathVariable long postId) {
        List<ReplyDTO> commentTreeForPost = replyService.getCommentTreeForPost(postId, null);
        return ResponseEntity.ok(commentTreeForPost);
    }

    /**
     * Creates a new reply to a post.
     *
     * @param token the authentication token for the user creating the reply
     * @param postId the ID of the post to which the reply is being made
     * @param request the ReplyRequest containing the reply text and optional parent reply ID
     * @return the newly created Reply, cast to a ReplyDTO
     */
    @PostMapping("/post/{postId}")
    public ResponseEntity<ReplyDTO> createReply(
            @RequestHeader("Authorization") String token,
            @PathVariable long postId,
            @RequestBody ReplyRequest request
    ) {
        long userId = jwtUtil.checkJwtAuthAndGetUserId(token);
        ApplicationUser user = userService.loadUserById(userId);

        Post post = postService.loadPostById(postId);
        Reply parentReply = request.getToReplyId() != 0
                ? replyService.findReplyById(request.getToReplyId())
                : null;

        // make reply
        Reply reply = replyService.createReply(post, user, request.getText(), parentReply);
        ReplyDTO dto = buildReplyDTO(reply, user);

        return ResponseEntity.ok(dto);
    }

    /**
     * Casts a vote on a reply.
     *
     * @param token the authentication token for the user casting the vote
     * @param voteDTO the VoteDTO containing the reply ID and vote type
     * @return the VoteDTO representing the cast vote
     */
    @PostMapping("/vote")
    public VoteDTO voteOnReply(@RequestHeader("Authorization") String token, @RequestBody VoteDTO voteDTO) {
        long userId = jwtUtil.checkJwtAuthAndGetUserId(token);
        replyService.userVoteOnReply(voteDTO.getVotableId(), userId, voteDTO.getVoteType());
        return voteDTO;
    }

    /**
     * Removes a user's vote from a reply.
     *
     * @param token the authentication token for the user removing their vote
     * @param votableId the ID of the reply from which to remove the vote
     * @return a no-content response indicating success
     */
    @DeleteMapping("/unvote")
    public ResponseEntity<Void> unvoteOnReply(@RequestHeader("Authorization") String token, @RequestParam long votableId) {
        long userId = jwtUtil.checkJwtAuthAndGetUserId(token);
        replyService.removeUserVoteOnReply(votableId, userId);
        return ResponseEntity.noContent().build();
    }

    /**
     * Retrieves the type of vote (if any) a user has cast on a reply.
     *
     * @param token the authentication token for the user
     * @param votableId the ID of the reply for which to retrieve the user's vote
     * @return the user's vote type (like, dislike, or null if no vote) or a no-content response if the user has not voted
     */
    @GetMapping("/get-vote")
    public ResponseEntity<UserVote.VoteType> checkUserVote(@RequestHeader("Authorization") String token, @RequestParam long votableId) {
        long userId = jwtUtil.checkJwtAuthAndGetUserId(token);
        Optional<UserVote.VoteType> voteType = replyService.getUserVoteOnReply(votableId, userId);

        return voteType
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.noContent().build());
    }

    /**
     * Builds a ReplyDTO from a Reply entity and the ApplicationUser who authored the reply.
     *
     * @param reply the Reply entity
     * @param user the ApplicationUser who authored the reply
     * @return the built ReplyDTO
     */
    private ReplyDTO buildReplyDTO(Reply reply, ApplicationUser user) {
        return ReplyDTO.builder()
                .id(reply.getId())
                .text(reply.getText())
                .author(
                        UserDTO.builder()
                                .userId(user.getId())
                                .username(user.getUsername())
                                .displayName(user.getDisplayName())
                                .profilePictureFileName(user.getProfilePictureFileName())
                                .build()
                )
                .datePosted(reply.getDatePosted())
                .likeCount(reply.getLikeCount())
                .dislikeCount(reply.getDislikeCount())
                .voteScore(reply.getVoteScore())
                .isDeleted(reply.isDeleted())
                .build();
    }
}
