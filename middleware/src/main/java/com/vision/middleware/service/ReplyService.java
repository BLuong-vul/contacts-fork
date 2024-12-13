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

/**
 * Service class responsible for managing replies, including creation, deletion, voting, and retrieval.
 * Provides functionality for constructing a comment tree for a given post.
 */
@Service
@RequiredArgsConstructor
public class ReplyService {

    /**
     * Maximum allowed length for a reply's text.
     */
    private static final int MAX_REPLY_LENGTH = 5000;

    /**
     * Maximum allowed nesting depth for replies.
     */
    private static final int MAX_NESTING_DEPTH = 100;

    /**
     * Repository for interacting with the reply data storage.
     */
    @Autowired
    private final ReplyRepository replyRepository;

    /**
     * Service for user-related operations.
     */
    @Autowired
    private final UserService userService;

    /**
     * Service for voting-related operations.
     */
    @Autowired
    private final VotingService votingService;

    /**
     * Creates a new reply for a given post. The reply can be a root reply (with no parent) or a child reply.
     *
     * @param post        the post to which the reply belongs
     * @param author      the author of the reply
     * @param text        the text content of the reply
     * @param parentReply the parent reply if this is a child reply, null otherwise
     * @return the created reply
     */
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

    /**
     * Retrieves a reply by its ID.
     *
     * @param replyId the ID of the reply to retrieve
     * @return the reply with the given ID, or throws an exception if not found
     * @throws IdNotFoundException if the reply with the given ID does not exist
     */
    public Reply findReplyById(long replyId) {
        return replyRepository.findById(replyId).orElseThrow(
                () -> new IdNotFoundException(String.format("Reply with id %d not found.", replyId))
        );
    }

    /**
     * Casts a vote on a reply.
     *
     * @param user     the user casting the vote
     * @param reply    the reply being voted on
     * @param voteType the type of vote (like/dislike)
     */
    public void voteOnReply(ApplicationUser user, Reply reply, UserVote.VoteType voteType) {
        votingService.voteOnVotable(user, reply, voteType);
    }

    /**
     * Casts a vote on a reply, retrieving the reply and user by their IDs.
     *
     * @param replyId  the ID of the reply being voted on
     * @param userId   the ID of the user casting the vote
     * @param voteType the type of vote (like/dislike)
     */
    public void userVoteOnReply(long replyId, long userId, UserVote.VoteType voteType) {
        Reply reply = replyRepository.findById(replyId).orElseThrow(
                () -> new IdNotFoundException("Reply id " + replyId + " not found.")
        );
        ApplicationUser user = userService.loadUserById(userId);

        votingService.voteOnVotable(user, reply, voteType);
    }

    /**
     * Removes a user's vote from a reply, retrieving the reply and user by their IDs.
     *
     * @param replyId the ID of the reply
     * @param userId  the ID of the user
     */
    public void removeUserVoteOnReply(long replyId, long userId) {
        Reply reply = replyRepository.findById(replyId).orElseThrow(
                () -> new IdNotFoundException("Reply id " + replyId + " not found.")
        );
        ApplicationUser user = userService.loadUserById(userId);

        votingService.deleteVote(user, reply);
    }

    /**
     * Retrieves the type of vote (if any) a user has cast on a reply, retrieving the reply and user by their IDs.
     *
     * @param replyId the ID of the reply
     * @param userId  the ID of the user
     * @return the vote type (like/dislike) if the user has voted, or an empty optional if not
     */
    public Optional<UserVote.VoteType> getUserVoteOnReply(long replyId, long userId) {
        Reply reply = replyRepository.findById(replyId).orElseThrow(
                () -> new IdNotFoundException("Reply id " + replyId + " not found.")
        );
        ApplicationUser user = userService.loadUserById(userId);

        return votingService.getUserVoteOnVotable(user, reply);
    }

    /**
     * Deletes a reply, either by soft-deletion (if it has child replies) or hard-deletion (if it does not).
     *
     * @param reply the reply to delete
     */
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

    /**
     * Constructs a comment tree for a given post, including the replies and their authors.
     *
     * @param postId     the ID of the post
     * @param currentUser the currently logged-in user (for determining vote status)
     * @return a list of ReplyDTOs representing the comment tree
     */
    public List<ReplyDTO> getCommentTreeForPost(Long postId, ApplicationUser currentUser) {
        List<Reply> topLevelReplies = replyRepository.findTopLevelRepliesByPostId(postId);
        return topLevelReplies.stream()
                .map(reply -> convertToCommentTree(reply, currentUser))
                .collect(Collectors.toList());
    }

    /**
     * Recursively converts a reply and its children into a ReplyDTO, including the author and vote information.
     *
     * @param reply      the reply to convert
     * @param currentUser the currently logged-in user (for determining vote status)
     * @return a ReplyDTO representing the reply and its children
     */
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
                .displayName(reply.getAuthor().getDisplayName())
                .profilePictureFileName(reply.getAuthor().getProfilePictureFileName())
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

    /**
     * Validates a reply's text and nesting depth (if applicable).
     *
     * @param text        the text content of the reply
     * @param parentReply the parent reply if this is a child reply, null otherwise
     * @throws IllegalArgumentException if the text is empty, too long, or if the nesting depth exceeds the maximum
     */
    private void validateReply(String text, Reply parentReply) {
        validateReplyLength(text);
        if (parentReply != null) {
            validateNestingDepth(parentReply);
        }
    }

    /**
     * Validates the length of a reply's text.
     *
     * @param text the text content of the reply
     * @throws IllegalArgumentException if the text is empty or too long
     */
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

    /**
     * Validates the nesting depth of a reply.
     *
     * @param parentReply the parent reply
     * @throws IllegalArgumentException if the nesting depth exceeds the maximum
     */
    private void validateNestingDepth(Reply parentReply) {
        int depth = calculateDepth(parentReply);
        if (depth >= MAX_NESTING_DEPTH) {
            throw new IllegalArgumentException(
                    String.format("Maximum nesting depth of %d exceeded", MAX_NESTING_DEPTH)
            );
        }
    }

    /**
     * Calculates the nesting depth of a reply.
     *
     * @param reply the reply
     * @return the nesting depth
     */
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
