package com.vision.middleware.service;

import com.vision.middleware.domain.ApplicationUser;
import com.vision.middleware.domain.Post;
import com.vision.middleware.dto.PostDTO;
import com.vision.middleware.repo.PostRepository;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import java.util.Date;

@RequiredArgsConstructor
@Service
@Transactional(rollbackOn = Exception.class) // <- each method call is treated as a single transaction.
public class PostService {

    // todo: decide on whether it is okay to access repositories directly through services, or if
    // services should expose functionality of the repositories...does it matter? maybe not.

    @Autowired
    private final PostRepository postRepository;

    @Autowired
    private final UserService userService;

    public Post createPost(PostDTO postDTO, long userId) {

        Date date = new Date();
        ApplicationUser postingUser = userService.loadUserById(userId);

        Post newPost = Post.builder()
                .postedBy(postingUser)
                .datePosted(date)
                .title(postDTO.getTitle())
                .text(postDTO.getText())
                .build();

        return postRepository.save(newPost);
    }

    public Page<Post> getAllPosts(int page, int size) {
        return postRepository.findAll(PageRequest.of(page, size, Sort.by("datePosted")));
    }

    public Page<Post> getAllPostsByUsername(String username, int page, int size) {
        ApplicationUser user = userService.loadUserByUsername(username); 
        return postRepository.findByPostedBy(user, PageRequest.of(page, size, Sort.by("datePosted")));
    }
}
