package com.vision.middleware.controller;

import com.vision.middleware.domain.ApplicationUser;
import com.vision.middleware.domain.Post;
import com.vision.middleware.domain.Reply;
import com.vision.middleware.domain.relations.UserVote;
import com.vision.middleware.dto.ReplyDTO;
import com.vision.middleware.dto.ReplyRequest;
import com.vision.middleware.dto.UserDTO;
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

/**
 * TODO: make it so that a user cannot vote on their own post / reply.
 */

@RestController
@RequestMapping("/replies")
@RequiredArgsConstructor
public class ReplyController {

    @Autowired
    private final ReplyService replyService;

    @Autowired
    private final UserService userService;

    @Autowired
    private final PostService postService;

    @Autowired
    private final VotingService votingService;

    @Autowired
    private final JwtUtil jwtUtil;

    @GetMapping("/post/{postId}")
    public ResponseEntity<List<ReplyDTO>> getRepliesToPostNoAuth(@PathVariable long postId) {
        List<ReplyDTO> commentTreeForPost = replyService.getCommentTreeForPost(postId, null);
        return ResponseEntity.ok(commentTreeForPost);
    }

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

        ReplyDTO dto = ReplyDTO.builder()
                .id(reply.getId())
                .text(reply.getText())
                .author(
                        UserDTO.builder()
                                .userId(user.getId())
                                .username(user.getUsername())
                                .build()
                )
                .datePosted(reply.getDatePosted())
                .likeCount(reply.getLikeCount())
                .dislikeCount(reply.getDislikeCount())
                .voteScore(reply.getVoteScore())
                .isDeleted(reply.isDeleted())
                .build();

        return ResponseEntity.ok(dto);
    }

    @PostMapping("/vote/{replyId}")
    public ResponseEntity<?> voteOnReply(
            @RequestHeader("Authorization") String token,
            @PathVariable long replyId,
            @RequestBody UserVote.VoteType voteType
    ) {
        long userId = jwtUtil.checkJwtAuthAndGetUserId(token);
        ApplicationUser user = userService.loadUserById(userId);
        Reply reply = replyService.findReplyById(replyId);

        votingService.voteOnVotable(user, reply, voteType);

        return ResponseEntity.ok("Vote created.");
    }
}
