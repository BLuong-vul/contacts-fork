package com.vision.middleware.config;

import com.vision.middleware.utils.RSAKeyProperties;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.util.Arrays;
import java.util.Collection;
import java.util.List;
import java.util.stream.Collectors;

/**
 * A filter that intercepts HTTP requests to validate JWT tokens and set the authentication in the context.
 * The filter expects the JWT token to be provided in the "Authorization" header with the "Bearer " prefix.
 */
@Component
@RequiredArgsConstructor
@Slf4j
public class TokenAuthenticationFilter extends OncePerRequestFilter {

    @Autowired
    private final RSAKeyProperties rsaKeyProperties;

    /**
     * Extracts and validates the JWT token from the Authorization header.
     * If the token is valid, it sets the authentication information in the SecurityContextHolder.
     *
     * @param request  the HTTP request
     * @param response the HTTP response
     * @param chain    the filter chain to continue processing
     * @throws ServletException if an error occurs during request processing
     * @throws IOException      if an I/O error occurs during request processing
     */
    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain chain) throws ServletException, IOException {
        String authorizationHeader = request.getHeader("Authorization");
        if (authorizationHeader != null && authorizationHeader.startsWith("Bearer ")) {
            String token = authorizationHeader.substring(7); // everything past bearer
            Authentication authentication = getAuthentication(token);
            if (authentication != null) {
                SecurityContextHolder.getContext().setAuthentication(authentication);
            }
        }
        chain.doFilter(request, response);
    }

    /**
     * Parses the JWT token and extracts the username and roles to create an Authentication object.
     * If the token is invalid, it logs an error and returns null.
     *
     * @param token the JWT token to parse
     * @return an Authentication object if the token is valid, null otherwise
     */
    private Authentication getAuthentication(String token) {
        try {
            Claims claims = Jwts.parserBuilder()
                    .setSigningKey(rsaKeyProperties.getPublicKey())
                    .build()
                    .parseClaimsJws(token)
                    .getBody();

            String username = claims.get("username", String.class);
            String rolesString = claims.get("roles", String.class);
            List<String> roles = Arrays.asList(rolesString.split(",")); //todo: verify what format this is...

            Collection<GrantedAuthority> authorities = roles.stream()
                    .map(SimpleGrantedAuthority::new)
                    .collect(Collectors.toList());

            return new UsernamePasswordAuthenticationToken(username, null, authorities);
        } catch (Exception e) {
            log.error("Failed to parse JWT token", e);
            return null;
        }
    }
}
