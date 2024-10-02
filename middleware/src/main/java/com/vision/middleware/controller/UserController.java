package com.vision.middleware.controller;

import com.vision.middleware.Application;
import com.vision.middleware.domain.ApplicationUser;
import com.vision.middleware.repo.UserRepository;
import com.vision.middleware.service.UserService;
import com.vision.middleware.utils.JwtUtil;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.web.bind.annotation.*;

import java.util.Collection;

@RestController
@RequestMapping("/user")
@CrossOrigin("*") // todo: change this later
@RequiredArgsConstructor
public class UserController {

    @Autowired
    private final UserService userService;

    @Autowired
    private final JwtUtil jwtUtil;

    @GetMapping("/")
    public String helloUserController() {
        return "User access level";
    }

    @GetMapping("/info")
    public UserDetails getUserInfo(@RequestHeader("Authorization") String token) {

        if (token != null && token.startsWith("Bearer ")) {
            token = token.substring(7);
            String username = jwtUtil.extractUsername(token);

            if (username != null && jwtUtil.isTokenValid(token, username)) {
                return userService.loadUserByUsername(username);
            }
        }

        return null;
    }

}
