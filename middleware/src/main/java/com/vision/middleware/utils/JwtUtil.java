package com.vision.middleware.utils;

import com.vision.middleware.config.Consts;
import com.vision.middleware.exceptions.InvalidTokenException;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.ExpiredJwtException;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.MalformedJwtException;
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

/**
 * Utility class for generating and validating JSON Web Tokens (JWTs).
 */
@Component
@RequiredArgsConstructor
public class JwtUtil {

    @Autowired
    private final RSAKeyProperties rsaKeyProperties;

    @Autowired
    private final JwtEncoder jwtEncoder;

    @Autowired
    private final JwtDecoder jwtDecoder;

    /**
     * Generates a JWT for a given authentication and user ID.
     *
     * @param auth   The authentication object containing user details and authorities.
     * @param userId The user ID.
     * @return The generated JWT token.
     */
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

    /**
     * Extracts claims from a JWT.
     *
     * @param token The JWT token.
     * @return The claims contained in the JWT.
     */
    public Claims extractClaims(String token) {
        return Jwts.parserBuilder()
                .setSigningKey(rsaKeyProperties.getPublicKey())
                .build()
                .parseClaimsJws(token)
                .getBody();
    }

    /**
     * Extracts the username (subject) from a JWT.
     *
     * @param token The JWT token.
     * @return The username contained in the JWT.
     */
    public String extractUsername(String token) {
        return extractClaims(token).getSubject();
    }

    /**
     * Checks if a JWT is valid.
     *
     * @param token The JWT token.
     * @return True if the token is valid, false otherwise.
     */
    public boolean isTokenValid(String token) {
        // note: this is where we would probably want to manually intervene
        //       with a blacklist if we want to deny a token for whatever reason.
        return !isTokenExpired(token);
    }

    /**
     * Checks if a JWT is expired.
     *
     * @param token The JWT token.
     * @return True if the token is expired, false otherwise.
     */
    public boolean isTokenExpired(String token) {
        try {
            return extractClaims(token).getExpiration().before(new Date());
        } catch (ExpiredJwtException e) {
            return true;
        }
    }

    /**
     * Extracts the user ID (subject) from a JWT.
     *
     * @param token The JWT token.
     * @return The user ID contained in the JWT.
     */
    public long extractId(String token) {
        String subject = extractClaims(token).getSubject();
        return Long.parseLong(subject);
    }

    /**
     * Checks if a JWT is valid and extracts the user ID.
     *
     * @param jwt The JWT token.
     * @return The user ID contained in the JWT.
     * @throws InvalidTokenException If the JWT is invalid.
     */
    public long checkJwtAuthAndGetUserId(String jwt) {

        try {
            if (jwt != null && jwt.startsWith("Bearer ")) {
                jwt = jwt.substring(7);
                long id = this.extractId(jwt);

                if (this.isTokenValid(jwt)) {
                    return id;
                }
            }
        } catch (MalformedJwtException e) {
            throw new InvalidTokenException("Malformed JWT");
        } catch (ExpiredJwtException e) {
            throw new InvalidTokenException("Expired JWT");
        }

        // checks above failed, jwt is invalid.
        throw new InvalidTokenException("Jwt not valid.");
    }
}
