package com.vision.middleware.service;

import com.vision.middleware.utils.JwtUtil;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Service;

@RequiredArgsConstructor
@Service
public class TokenService {

    @Autowired
    private final JwtUtil jwtUtil;

    public String generateJwt(Authentication auth) {
        return jwtUtil.generateToken(auth);
    }
}
