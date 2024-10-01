package com.vision.middleware.controller;

import com.vision.middleware.Application;
import com.vision.middleware.domain.ApplicationUser;
import com.vision.middleware.repo.UserRepository;
import com.vision.middleware.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Collection;

@RestController
@RequestMapping("/user")
@CrossOrigin("*") // todo: change this later
@RequiredArgsConstructor
public class UserController {

    @Autowired
    private final UserService userService;

    @GetMapping("/")
    public String helloUserController() {
        return "User access level";
    }

    @GetMapping("/info")
    public UserDetails getUserInfo() {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();

        if (auth != null && auth.isAuthenticated()) {
            String username = auth.getName();
            Collection<? extends GrantedAuthority> authorities = auth.getAuthorities();
            return userService.loadUserByUsername(username);
        } else {
            return null;
        }
    }

}
