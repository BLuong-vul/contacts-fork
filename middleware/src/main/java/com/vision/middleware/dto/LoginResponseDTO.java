package com.vision.middleware.dto;

import com.vision.middleware.domain.Role;
import lombok.*;
import org.springframework.security.core.GrantedAuthority;

import java.util.Collection;
import java.util.Set;

/**
 * Data Transfer Object for representing a login response.
 * It includes the username, user ID, roles, and JWT token.
 */
@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
@Builder
public class LoginResponseDTO {
    private String username;
    private long userId;
    private Collection<? extends GrantedAuthority> roles;
    private String jwt;
}
