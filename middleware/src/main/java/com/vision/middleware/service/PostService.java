package com.vision.middleware.service;

import com.vision.middleware.domain.ApplicationUser;
import com.vision.middleware.domain.Post;
import com.vision.middleware.dto.PostDTO;
import com.vision.middleware.repo.PostRepository;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.sql.Date;

@RequiredArgsConstructor
@Service
@Transactional(rollbackOn = Exception.class) // <- each method call is treated as a single transaction.
public class PostService {

    // todo: decide on whether it is okay to access repositories directly through services, or if
    // services should expose functionality of the repositories...

    @Autowired
    private final PostRepository postRepository;

    @Autowired
    private final UserService userService;

    public void createPost(PostDTO postDTO, long userId) {

        java.util.Date utilDate = new java.util.Date();
        Date sqlDate = new Date(utilDate.getTime());

        ApplicationUser postingUser = userService.loadUserById(userId);

        Post newPost = Post.builder()
                .postedBy(postingUser)
                .datePosted(sqlDate)
                .title(postDTO.getTitle())
                .text(postDTO.getText())
                .build();

        postRepository.save(newPost);
    }
}
