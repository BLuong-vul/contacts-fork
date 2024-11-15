package com.vision.middleware.service;

import com.vision.middleware.domain.ApplicationUser;
import com.vision.middleware.domain.Post;
import com.vision.middleware.domain.Reply;
import com.vision.middleware.domain.relations.UserVote;
import com.vision.middleware.dto.ReplyDTO;
import com.vision.middleware.repo.ReplyRepository;

import jakarta.persistence.EntityNotFoundException;
import lombok.Builder;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;
import java.util.Date;

@Service
@RequiredArgsConstructor
@Builder
public class ReplyService {

    @Autowired
    private final UserService userService;
    private final PostService postService;
    
    // todo: cast tree of replies that will be returned into DTO, or at least have a fucntion that is able to do that.
    // what information does the front end need to be able to properly construct a post?
    // should the DTOs be nested for required information?

    @Autowired
    private final ReplyRepository replyRepository;

    @Autowired
    private final VotingService votingService;

    // null parent reply if this is supposed to be a root reply.
    public Reply createReply(Post post, ApplicationUser author, String text, Reply parentReply) {
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

        if (parentReply != null && parentReply.getLevel() >= 100) {
            throw new IllegalArgumentException("Maximum nesting level exceeded");
        }

        return replyRepository.save(reply);
    }

    public void voteOnReply(ApplicationUser user, Reply reply, UserVote.VoteType voteType) {
        votingService.voteOnVotable(user, reply, voteType);
    }

    public void deleteReply(Reply reply) {
        if (!reply.getChildReplies().isEmpty()) {
            // preserve tree structure
            reply.softDelete();
            replyRepository.save(reply);
        } else {
            // delete that thing
            replyRepository.delete(reply);
        }
    }

    public List<Reply> getRepliesForPost(Long postId) {
        return replyRepository.findTopLevelRepliesByPostId(postId);
    }

    public void updateVote(Reply reply, boolean isUpvote) {
        if (isUpvote) {
            reply.setLikeCount(reply.getLikeCount() + 1);
        } else {
            reply.setDislikeCount(reply.getDislikeCount() + 1);
        }
        replyRepository.save(reply);
    }

}
