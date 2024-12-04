package com.vision.demo.utils;

import com.vision.middleware.exceptions.InvalidTokenException;
import com.vision.middleware.utils.JwtUtil;
import com.vision.middleware.utils.KeyGeneratorUtility;
import com.vision.middleware.utils.RSAKeyProperties;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.security.oauth2.jwt.JwtDecoder;
import org.springframework.security.oauth2.jwt.JwtEncoder;
import org.springframework.security.oauth2.jwt.JwtEncoderParameters;

import java.security.KeyPair;
import java.security.interfaces.RSAPrivateKey;
import java.security.interfaces.RSAPublicKey;
import java.time.Instant;
import java.util.Collection;
import java.util.Collections;
import java.util.Date;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class JwtUtilTest {

    @Mock
    private RSAKeyProperties rsaKeyProperties;

    @Mock
    private JwtEncoder jwtEncoder;

    @Mock
    private JwtDecoder jwtDecoder;

    @InjectMocks
    private JwtUtil jwtUtil;

    private String mockToken;
    private String expiredMockToken;
    private String subjectlessMockToken;

    private RSAPublicKey publicKey;

    @BeforeEach
    void setUp() {
        // Create a mock secret key
        KeyPair keyPair = KeyGeneratorUtility.generateRsaKey();
        publicKey = (RSAPublicKey) keyPair.getPublic();
        RSAPrivateKey privateKey = (RSAPrivateKey) keyPair.getPrivate();

        // Create mock claims
        Claims mockClaims = Jwts.claims()
                .setSubject("1234567890")
                .setIssuedAt(Date.from(Instant.now()))
                .setExpiration(Date.from(Instant.now().plusSeconds(3600)));

        // Create a mock token
        mockToken = Jwts.builder()
                .setClaims(mockClaims)
                .signWith(privateKey)
                .compact();

        // now create the expired token
        Claims mockExpiredClaims = Jwts.claims()
                .setSubject("1234567890")
                .setIssuedAt(Date.from(Instant.now().minusMillis(999999)))
                .setExpiration(Date.from(Instant.now().minusMillis(888888)));

        expiredMockToken = Jwts.builder()
                .setClaims(mockExpiredClaims)
                .signWith(privateKey)
                .compact();
    }

    @Test
    @SuppressWarnings("unchecked") // we have to cast to a raw type Collection to make this work.
    void testGenerateToken() {
        Authentication auth = mock(Authentication.class);
        when(auth.getAuthorities()).thenReturn((Collection) Collections.singletonList(new SimpleGrantedAuthority("USER")));

        Jwt jwt = mock(Jwt.class);
        when(jwt.getTokenValue()).thenReturn(mockToken);

        when(jwtEncoder.encode(any(JwtEncoderParameters.class))).thenReturn(jwt);

        String token = jwtUtil.generateToken(auth, 1234567890L);

        assertNotNull(token);
        assertEquals(mockToken, token);
    }

    @Test
    void testExtractClaims() {
        when(rsaKeyProperties.getPublicKey()).thenReturn(publicKey);

        Claims claims = jwtUtil.extractClaims(mockToken);

        assertNotNull(claims);
        assertEquals("1234567890", claims.getSubject());
    }

    @Test
    void testExtractUsername() {
        when(rsaKeyProperties.getPublicKey()).thenReturn(publicKey);

        String username = jwtUtil.extractUsername(mockToken);

        assertNotNull(username);
        assertEquals("1234567890", username);
    }

    @Test
    void testIsTokenValid() {
        when(rsaKeyProperties.getPublicKey()).thenReturn(publicKey);

        boolean isValid = jwtUtil.isTokenValid(mockToken);

        assertTrue(isValid);
    }

    @Test
    void testIsTokenExpired() {
        when(rsaKeyProperties.getPublicKey()).thenReturn(publicKey);

        boolean isExpired = jwtUtil.isTokenExpired(mockToken);

        assertFalse(isExpired);
    }

    @Test
    void testIsTokenExpired_ExpiredToken() {
        when(rsaKeyProperties.getPublicKey()).thenReturn(publicKey);

        boolean isExpired = jwtUtil.isTokenExpired(expiredMockToken);

        assertTrue(isExpired);
    }

    @Test
    void testExtractId() {
        when(rsaKeyProperties.getPublicKey()).thenReturn(publicKey);

        long id = jwtUtil.extractId(mockToken);

        assertEquals(1234567890L, id);
    }

    @Test
    void testCheckJwtAuthAndGetUserId() {
        when(rsaKeyProperties.getPublicKey()).thenReturn(publicKey);

        String bearerToken = "Bearer " + mockToken;
        long id = jwtUtil.checkJwtAuthAndGetUserId(bearerToken);

        assertEquals(1234567890L, id);
    }

    @Test
    void testCheckJwtAuthAndGetUserId_MalformedToken() {
        when(rsaKeyProperties.getPublicKey()).thenReturn(publicKey);

        String invalidToken = "Bearer invalidToken";

        assertThrows(InvalidTokenException.class, () -> jwtUtil.checkJwtAuthAndGetUserId(invalidToken));
    }

    @Test
    void testCheckJwtAuthAndGetUserId_NullToken() {
        assertThrows(InvalidTokenException.class, () -> jwtUtil.checkJwtAuthAndGetUserId(null));
    }

    @Test
    void testCheckJwtAuthAndGetUserId_ExpiredToken() {
        when(rsaKeyProperties.getPublicKey()).thenReturn(publicKey);

        String bearerToken = "Bearer " + expiredMockToken;

        assertThrows(InvalidTokenException.class, () -> jwtUtil.checkJwtAuthAndGetUserId(bearerToken));
    }
}