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

    public Reply creatReply(ReplyDTO replyDTO, Long postId, Long userId){
        Date date = new Date();
        ApplicationUser postingUser = userService.loadUserById(userId);
        Post post = postService.loadPostById(postId);
        /*Design Pattern: Builder*/
        Reply newReply = Reply.builder()
        .author(postingUser)
        .post(post)
        .datePosted(date)
        .text(replyDTO.getText())
        .parentReply(replyDTO.getParentReply()) // If parentReply is nullable, this can be null
        .build();
        /*Design Pattern: Builder*/
        return replyRepository.save(newReply);
    }
    
    public List<Reply> getReplyTree(Long postId) {
        //Old version
        //List<Reply> rootReplies = replyRepository.findByReplyId(postId);

        // Retrieve only the root replies for the specified post
        List<Reply> rootReplies = replyRepository.findByReplyIdAndParentReplyReplyIdIsNull(postId);
        
        for (Reply reply : rootReplies) {
            populateChildReplies(reply);
        }

        return rootReplies;
    }

    // todo: stop working on this and actually start working on the thing that we need to have done by this week :(((((((
    private void populateChildReplies(Reply reply) {
        Set<Reply> childReplies = new LinkedHashSet<>(replyRepository.findByParentReplyReplyId(reply.getReplyId()));

        reply.setChildReplies(childReplies);

        for (Reply child : childReplies) {
            populateChildReplies(child); // recurse
        }
    }

}
