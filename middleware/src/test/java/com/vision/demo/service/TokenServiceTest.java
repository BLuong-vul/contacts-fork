package com.vision.demo.service;

import com.vision.middleware.service.TokenService;
import com.vision.middleware.utils.JwtUtil;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.core.Authentication;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class TokenServiceTest {

    @InjectMocks
    private TokenService tokenService;

    @Mock
    private JwtUtil jwtUtil;

    private Authentication auth;

    @Test
    void testGenerateJwt() {
        long userId = 12L;
        String expectedToken = "generated-jwt-:)";
        when(jwtUtil.generateToken(auth, userId)).thenReturn(expectedToken);

        String actualToken = tokenService.generateJwt(auth, userId);

        assertThat(actualToken).isEqualTo(expectedToken);
    }

}
