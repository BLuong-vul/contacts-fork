package com.vision.middleware.service;

import com.vision.middleware.domain.Reply;
import com.vision.middleware.repo.ReplyRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;

@Service
@RequiredArgsConstructor
public class ReplyService {


    // todo: cast tree of replies that will be returned into DTO, or at least have a fucntion that is able to do that.
    // what information does the front end need to be able to properly construct a post?
    // should the DTOs be nested for required information?

    @Autowired
    private final ReplyRepository replyRepository;

    public List<Reply> getReplyTree(Long postId) {

        List<Reply> rootReplies = replyRepository.findByReplyId(postId); // todo: does this return all posts and not just root posts?

        for (Reply reply : rootReplies) {
            populateChildReplies(reply);
        }

        return rootReplies;
    }

    // todo: stop working on this and actually start working on the thing that we need to have done by this week :(((((((
    private void populateChildReplies(Reply reply) {
        Set<Reply> childReplies = new LinkedHashSet<>(replyRepository.findByParentReplyReplyId(reply.getId()));

        reply.setChildReplies(childReplies);

        for (Reply child : childReplies) {
            populateChildReplies(child); // recurse
        }
    }

}
