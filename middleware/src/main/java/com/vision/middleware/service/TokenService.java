package com.vision.middleware.service;

import com.vision.middleware.utils.JwtUtil;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Service;

/**
 * Service responsible for generating JSON Web Tokens (JWTs) for authenticated users.
 */
@RequiredArgsConstructor
@Service
public class TokenService {

    /**
     * Instance of JwtUtil, utilized for JWT generation and validation.
     */
    @Autowired
    private final JwtUtil jwtUtil;

    /**
     * Generates a JSON Web Token (JWT) for the given authenticated user.
     *
     * @param auth    the authenticated user's authentication object
     * @param userId  the unique identifier of the authenticated user
     * @return the generated JWT as a string
     */
    public String generateJwt(Authentication auth, long userId) {
        return jwtUtil.generateToken(auth, userId);
    }
}
