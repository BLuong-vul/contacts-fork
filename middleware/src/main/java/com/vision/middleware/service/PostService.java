package com.vision.middleware.service;

import com.vision.middleware.domain.ApplicationUser;
import com.vision.middleware.domain.MediaPost;
import com.vision.middleware.domain.Post;
import com.vision.middleware.domain.relations.UserVote;
import com.vision.middleware.dto.PostDTO;
import com.vision.middleware.exceptions.DuplicateVoteException;
import com.vision.middleware.exceptions.IdNotFoundException;
import com.vision.middleware.repo.PostRepository;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import java.util.Date;
import java.util.Optional;

@RequiredArgsConstructor
@Service
@Transactional(rollbackOn = Exception.class) // <- each method call is treated as a single transaction.
public class PostService {

    @Autowired
    private final PostRepository postRepository;

    @Autowired
    private final VoteRepository voteRepository;

    @Autowired
    private final UserService userService;

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

    public void userVoteOnPost(long postId, long userId, UserVote.VoteType voteType) {
        Post post = postRepository.findByPostId(postId).orElseThrow(
                () -> new IdNotFoundException("Post id " + postId + " not found.")
        );
        ApplicationUser user = userService.loadUserById(userId);

        Optional<UserVote> optionalVote = voteRepository.findByUserAndPost(user, post);
        optionalVote.ifPresentOrElse(
                vote -> updateVote(vote, voteType),
                () -> createVote(post, user, voteType)
        );
    }

    private void updateVote(UserVote vote, UserVote.VoteType voteType) {
        if (vote.getVoteType() != voteType) {
            vote.setVoteType(voteType);
            voteRepository.save(vote);
        } else {
            throw new DuplicateVoteException("User vote for post already exists.");
        }
    }

    private void createVote(Post post, ApplicationUser user, UserVote.VoteType voteType) {
        UserVote vote = UserVote.builder()
                .votable(post)
                .user(user)
                .voteType(voteType)
                .build();
        voteRepository.save(vote);
    }

    public Page<Post> getAllPosts(int page, int size) {
        return postRepository.findAll(PageRequest.of(page, size, Sort.by("datePosted")));
    }
}
