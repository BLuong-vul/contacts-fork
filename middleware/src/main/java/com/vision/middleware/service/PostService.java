package com.vision.middleware.service;

import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@RequiredArgsConstructor
@Service
@Transactional(rollbackOn = Exception.class) // <- each method call is treated as a single transaction.
public class PostService {
    /*
    todo: allow creation of posts, using the JWT of the user that is posting.
        should we have the image uploaded in its individual request?
        or should the image be uploaded with all the other information for creating a post?

        Regardless, we will need some kind of DTO to facilitate this as well.

     */
}
