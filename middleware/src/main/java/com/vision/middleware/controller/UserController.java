package com.vision.middleware.controller;

import com.vision.middleware.service.UserService;
import com.vision.middleware.utils.JwtUtil;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;

import com.vision.middleware.dto.UserDTO;

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
        long id = jwtUtil.checkJwtAuthAndGetUserId(token);
        return userService.loadUserById(id);
    }

    @GetMapping("/username/{id}")
    public String getUsernameById(@RequestHeader("Authorization") String token, @PathVariable String id){
        long userId = Long.parseLong(id);
        UserDetails userDetails = userService.loadUserById(userId);
        return userDetails.getUsername();
    }

    @GetMapping("/id/{username}")
    public Long getIdByUsername(@PathVariable String username){
        long userId = userService.loadUserByUsername(username).getUserId();
        return userId;
    }
}
