package com.vision.middleware.service;

import com.vision.middleware.domain.ApplicationUser;
import com.vision.middleware.domain.Post;
import com.vision.middleware.domain.Reply;
import com.vision.middleware.domain.relations.UserVote;
import com.vision.middleware.dto.ReplyDTO;
import com.vision.middleware.dto.UserDTO;
import com.vision.middleware.exceptions.IdNotFoundException;
import com.vision.middleware.repo.ReplyRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Comparator;
import java.util.Date;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class ReplyService {

    private static final int MAX_REPLY_LENGTH = 5000;
    private static final int MAX_NESTING_DEPTH = 100;

    @Autowired
    private final ReplyRepository replyRepository;

    @Autowired
    private final VotingService votingService;

    // null parent reply if this is supposed to be a root reply.
    @Transactional
    public Reply createReply(Post post, ApplicationUser author, String text, Reply parentReply) {
        validateReply(text, parentReply);

        Reply reply = Reply.builder()
                .post(post)
                .author(author)
                .text(text)
                .parentReply(parentReply)
                .datePosted(new Date())
                .likeCount(0L)
                .dislikeCount(0L)
                .voteScore(0L)
                .build();

        if (parentReply != null) {
            // JPA automatically cascades change to parents when calling save later on child. (CascadeType.ALL on childReplies)
            parentReply.addChildReply(reply);
        }

        return replyRepository.save(reply);
    }

    public Reply findReplyById(long replyId) {
        return replyRepository.findById(replyId).orElseThrow(
                () -> new IdNotFoundException(String.format("Reply with id %d not found.", replyId))
        );
    }

    public void voteOnReply(ApplicationUser user, Reply reply, UserVote.VoteType voteType) {
        votingService.voteOnVotable(user, reply, voteType);
    }

    @Transactional
    public void deleteReply(Reply reply) {
        if (!reply.getChildReplies().isEmpty()) {
            // preserve tree structure
            reply.softDelete();
            replyRepository.save(reply);
        } else {
            // delete that thing. Update parent that we are removing one of its children as well.
            Reply parentReply = reply.getParentReply();
            parentReply.removeChildReply(reply);
            replyRepository.save(parentReply);

            replyRepository.delete(reply);
        }
    }

    public List<ReplyDTO> getCommentTreeForPost(Long postId, ApplicationUser currentUser) {
        List<Reply> topLevelReplies = replyRepository.findTopLevelRepliesByPostId(postId);
        return topLevelReplies.stream()
                .map(reply -> convertToCommentTree(reply, currentUser))
                .collect(Collectors.toList());
    }

    private ReplyDTO convertToCommentTree(Reply reply, ApplicationUser currentUser) {
        // Get the user's vote on this reply if it exists
        Optional<UserVote.VoteType> userVoteType = votingService.getUserVoteOnVotable(currentUser, reply);

        // Convert child replies recursively
        List<ReplyDTO> childComments = reply.getChildReplies().stream()
                .sorted(Comparator
                        .comparingLong(Reply::getVoteScore).reversed()
                        .thenComparing(Reply::getDatePosted).reversed()) // Note the reversed() for date
                .map(childReply -> convertToCommentTree(childReply, currentUser))
                .collect(Collectors.toList());

        // Build author DTO
        UserDTO author = UserDTO.builder()
                .userId(reply.getAuthor().getId())
                .username(reply.getAuthor().getUsername())
                .build();

        // Build the comment tree DTO
        return ReplyDTO.builder()
                .id(reply.getId())
                .text(reply.getText())
                .author(author)
                .datePosted(reply.getDatePosted())
                .likeCount(reply.getLikeCount())
                .dislikeCount(reply.getDislikeCount())
                .voteScore(reply.getVoteScore())
                .userVoteType(userVoteType.orElse(null))
                .isDeleted(reply.isDeleted())
                .replies(childComments)
                .build();
    }

    private void validateReply(String text, Reply parentReply) {
        validateReplyLength(text);
        if (parentReply != null) {
            validateNestingDepth(parentReply);
        }
    }

    private void validateReplyLength(String text) {
        if (text == null || text.trim().isEmpty()) {
            throw new IllegalArgumentException("Reply text cannot be empty");
        }
        if (text.length() > MAX_REPLY_LENGTH) {
            throw new IllegalArgumentException(
                    String.format("Reply text exceeds maximum length of %d characters", MAX_REPLY_LENGTH)
            );
        }
    }

    private void validateNestingDepth(Reply parentReply) {
        int depth = calculateDepth(parentReply);
        if (depth >= MAX_NESTING_DEPTH) {
            throw new IllegalArgumentException(
                    String.format("Maximum nesting depth of %d exceeded", MAX_NESTING_DEPTH)
            );
        }
    }

    private int calculateDepth(Reply reply) {
        int depth = 0;
        Reply current = reply;
        while (current.getParentReply() != null) {
            depth++;
            current = current.getParentReply();
        }
        return depth;
    }
}
