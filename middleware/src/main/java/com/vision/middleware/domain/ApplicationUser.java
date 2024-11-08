package com.vision.middleware.domain;

import com.vision.middleware.domain.relations.UserFollows;
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
    private long id;

    @Column(name = "username", unique = true)
    private String username;

    @Column(length = 64)
    private String password;
    private String fullName;

    @Column(unique = true, length = 128)
    private String email;

    @Column(unique = true, length = 15)
    private String phoneNumber;

    @Column(unique = true, length = 256)
    private String address;
    private String city;
    private String state;
    private String zipCode;
    private String country;
    private long followerCount;

    @Column(length = 200)
    private String bio;

    @OneToMany(mappedBy = "followee", fetch = FetchType.LAZY, cascade = CascadeType.ALL)
    private Set<UserFollows> followers;

    @OneToMany(mappedBy = "follower", fetch = FetchType.LAZY, cascade = CascadeType.ALL)
    private Set<UserFollows> following;

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
