package com.vision.middleware.controller;

import com.vision.middleware.domain.ApplicationUser;
import com.vision.middleware.dto.LoginDTO;
import com.vision.middleware.dto.LoginResponseDTO;
import com.vision.middleware.dto.RegistrationDTO;
import com.vision.middleware.service.AuthenticationService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/auth")
@CrossOrigin("*") // todo: update this later
public class AuthenticationController {

    @Autowired
    private AuthenticationService authenticationService;

    @PostMapping("/register")
    public ApplicationUser registerUser(@RequestBody RegistrationDTO body) {
        // todo: figure out error handling
        return authenticationService.registerUser(body);
    }

    @PostMapping("/login")
    public LoginResponseDTO loginUser(@RequestBody LoginDTO body) {
        return authenticationService.loginUser(body.getUsername(), body.getPassword());
    }

}
