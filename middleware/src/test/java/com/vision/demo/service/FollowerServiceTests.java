package com.vision.demo.service;

import com.vision.middleware.repo.UserFollowsRepository;
import com.vision.middleware.service.FollowerService;
import com.vision.middleware.service.UserService;
import com.vision.middleware.utils.JwtUtil;
import jakarta.inject.Inject;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.beans.factory.annotation.Autowired;

@ExtendWith(MockitoExtension.class)
public class FollowerServiceTests {

    @InjectMocks
    private FollowerService followerService;

    @Mock
    private UserFollowsRepository followsRepository;

    @Mock
    private UserService userService;

    @Mock
    private JwtUtil jwtUtil;


}
