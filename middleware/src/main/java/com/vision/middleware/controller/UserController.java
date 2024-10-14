package com.vision.middleware.controller;

import com.vision.middleware.dto.UserDTO;
import com.vision.middleware.service.UserService;
import com.vision.middleware.utils.JwtUtil;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;

import java.util.List;

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

    @GetMapping("/id/{username}")
    public Long getIdByUsername(@PathVariable String username){
        return userService.loadUserByUsername(username).getUserId();
    }

    @GetMapping("/friend/list")
    public List<UserDTO> getFriends(@RequestHeader("Authorization") String token) {
        long id = jwtUtil.checkJwtAuthAndGetUserId(token);


        return null; //todo: implement
    }
}
