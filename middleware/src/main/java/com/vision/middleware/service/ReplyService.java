package com.vision.middleware.service;

import com.vision.middleware.domain.ApplicationUser;
import com.vision.middleware.domain.Post;
import com.vision.middleware.domain.Reply;
import com.vision.middleware.dto.ReplyDTO;
import com.vision.middleware.repo.ReplyRepository;

import lombok.Builder;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;
import java.util.Date;
import java.util.ArrayList;
import java.util.stream.Collectors;
import java.util.Map;

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

    public Reply createReply(ReplyDTO replyDTO, Long postId, Long userId){
        Date date = new Date();
        ApplicationUser postingUser = userService.loadUserById(userId);
        Post post = postService.loadPostById(postId);
        Reply parentReply = null;
        if (replyDTO.getParentReplyId() != null) {
            parentReply = replyRepository.findById(replyDTO.getParentReplyId())
                .orElseThrow(() -> new RuntimeException("Parent reply not found"));
        }

        /*Design Pattern: Builder*/
        Reply newReply = Reply.builder()
        .author(postingUser)
        .post(post)
        .datePosted(date)
        .text(replyDTO.getText())
        .parentReply(parentReply) // If parentReply is nullable, this can be null
        .build();
        /*Design Pattern: Builder*/
        return replyRepository.save(newReply);
    }
    
    public List<Reply> getReplyTree(Long postId) {
        List<Reply> replies = replyRepository.findByPostId(postId);

        Map<Long, Reply> replyMap = replies.stream().collect(Collectors.toMap(Reply::getId, reply -> reply));

        List<Reply> rootReplies = new ArrayList<>();

        for (Reply reply : replies) {
            if (reply.getParentReply() != null) {
                Reply parent = replyMap.get(reply.getParentReply().getId());
                if (parent != null) {
                    parent.getChildReplies().add(reply);
                }
            } else {
                rootReplies.add(reply);
            }
        }

        return rootReplies;
    }

    // todo: stop working on this and actually start working on the thing that we need to have done by this week :(((((((
    private void populateChildReplies(Reply reply) {
//        Set<Reply> childReplies = new LinkedHashSet<>(replyRepository.findByParentReplyReplyId(reply.getId()));
//
//        reply.setChildReplies(childReplies);
//
//        for (Reply child : childReplies) {
//            populateChildReplies(child); // recurse
//        }
    }

}
