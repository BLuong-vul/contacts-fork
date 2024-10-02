package com.vision.middleware.utils;


import com.vision.middleware.config.Consts;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.oauth2.jwt.JwtClaimsSet;
import org.springframework.security.oauth2.jwt.JwtDecoder;
import org.springframework.security.oauth2.jwt.JwtEncoder;
import org.springframework.security.oauth2.jwt.JwtEncoderParameters;
import org.springframework.stereotype.Component;

import java.time.Instant;
import java.util.Date;
import java.util.stream.Collectors;

@Component
@RequiredArgsConstructor
public class JwtUtil {

    @Autowired
    private final RSAKeyProperties rsaKeyProperties;

    @Autowired
    private final JwtEncoder jwtEncoder;

    @Autowired
    private final JwtDecoder jwtDecoder;

    public String generateToken(Authentication auth) {
        Instant now = Instant.now(); // time token issued
        Instant expTime = now.plusMillis(Consts.SESSION_TIMEOUT_MILLIS); // time token should expire

        // go through all authorities in auth and map -> granted authority. (Role implements GrantedAuthority)
        String scope = auth.getAuthorities().stream()
                .map(GrantedAuthority::getAuthority)
                .collect(Collectors.joining(" "));

        JwtClaimsSet claims = JwtClaimsSet.builder()
                .issuer("self")
                .issuedAt(now)
                .subject(auth.getName())
                .claim("roles", scope) // can add more stuff here, we just need roles though.
                .expiresAt(expTime)
                .build();

        return jwtEncoder.encode(JwtEncoderParameters.from(claims)).getTokenValue();
    }

    public Claims extractClaims(String token) {
        return Jwts.parserBuilder()
                .setSigningKey(rsaKeyProperties.getPublicKey())
                .build()
                .parseClaimsJws(token)
                .getBody();
    }

    // todo: error handling for these methods
    public String extractUsername(String token) {
        return extractClaims(token).getSubject();
    }

    public boolean isTokenValid(String token, String username) {
        return extractUsername(token).equals(username) && !isTokenExpired(token);
    }

    private boolean isTokenExpired(String token) {
        return extractClaims(token).getExpiration().before(new Date());
    }
}
