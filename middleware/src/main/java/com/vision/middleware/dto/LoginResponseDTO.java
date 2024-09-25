package com.vision.middleware.dto;

import com.vision.middleware.domain.Role;
import lombok.*;
import org.springframework.security.core.GrantedAuthority;

import java.util.Collection;
import java.util.Set;

@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
@Builder
public class LoginResponseDTO {

    private String username;
    private String email;
    private Collection<? extends GrantedAuthority> roles;
    private String jwt;

}
