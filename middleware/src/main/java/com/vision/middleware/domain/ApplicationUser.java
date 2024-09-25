package com.vision.middleware.domain;

import jakarta.persistence.*;
import lombok.*;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import java.util.Collection;
import java.util.Set;

@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@Table(name = "application_users")
@Entity
@Builder
public class ApplicationUser implements UserDetails {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "user_id")
    private long userId;

    @Column(name = "username", unique = true)
    private String username;

    private String password;

    private String fullName;

    @Column(unique = true)
    private String email;

    @Column(unique = true)
    private String phoneNumber;

    private String address;

    private String city;

    private String state;

    private String zipCode;

    private String country;

    private long followerCount;

    @ManyToMany(fetch = FetchType.EAGER) // many users can have many roles. Eager because there shouldn't be too many roles.
    @JoinTable(
        name = "user_role_junction",
        joinColumns = {@JoinColumn(name = "user_id")},
        inverseJoinColumns = {@JoinColumn(name = "role_id")}
    )
    private Set<Role> authorities;

    @OneToMany(fetch = FetchType.LAZY)
    private Set<Post> posts;

    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        return this.authorities;
    }
}
