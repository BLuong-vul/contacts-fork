package com.vision.middleware.domain;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.springframework.security.core.GrantedAuthority;

/**
 * Represents a role in the system that can be assigned to a user.
 * Each role has a unique identifier and an authority which specifies the role's permission.
 */
@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
@Entity
@Table(name = "roles")
public class Role implements GrantedAuthority {
    /**
     * The unique identifier for the role.
     */
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "role_id")
    private Long roleId;

    /**
     * The authority of the role, which is the permission or level of access that this role grants.
     */
    private String authority;

    /**
     * Constructs a new role with the given authority.
     * @param authority the permission or level of access that this role grants.
     */
    public Role(String authority) {
        this.authority = authority;
    }

}
