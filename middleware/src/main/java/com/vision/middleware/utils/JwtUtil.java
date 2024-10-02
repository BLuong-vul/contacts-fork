package com.vision.middleware.utils;


import com.vision.middleware.config.Consts;
import com.vision.middleware.exceptions.InvalidTokenException;
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

    public String generateToken(Authentication auth, long userId) {
        Instant now = Instant.now(); // time token issued
        Instant expTime = now.plusMillis(Consts.SESSION_TIMEOUT_MILLIS); // time token should expire

        // go through all authorities in auth and map -> granted authority. (Role implements GrantedAuthority)
        String scope = auth.getAuthorities().stream()
                .map(GrantedAuthority::getAuthority)
                .collect(Collectors.joining(" "));

        JwtClaimsSet claims = JwtClaimsSet.builder()
                .issuer("self")
                .issuedAt(now)
                .subject(String.valueOf(userId))
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

    public boolean isTokenValid(String token) {
        // note: this is where we would probably want to manually intervene
        //       with a blacklist if we want to deny a token for whatever reason.
        return !isTokenExpired(token);
    }

    private boolean isTokenExpired(String token) {
        return extractClaims(token).getExpiration().before(new Date());
    }

    public long extractId(String token) {
        String subject = extractClaims(token).getSubject();
        if (subject == null) {
            throw new InvalidTokenException("Token subject is invalid");
        }
        return Long.parseLong(subject);
    }

    public long checkJwtAuthAndGetUserId(String jwt) {

        if (jwt != null && jwt.startsWith("Bearer ")) {
            jwt = jwt.substring(7);
            long id = this.extractId(jwt);

            if (this.isTokenValid(jwt)) {
                return id;
            }
        }

        // checks above failed, jwt is invalid.
        throw new InvalidTokenException("Jwt not valid.");
    }
}
