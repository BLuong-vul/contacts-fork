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

    @NonNull
    @Column(name = "username", unique = true)
    private String username;

    @NonNull
    private String password;

    @NonNull
    private String fullName;

    @NonNull
    @Column(unique = true)
    private String email;

    @NonNull
    @Column(unique = true)
    private String phoneNumber;

    @NonNull private String address;
    @NonNull private String city;
    @NonNull private String state;
    @NonNull private String zipCode;
    @NonNull private String country;
    private long followerCount; // TODO: decide to remove this or not?

    private String profilePictureFileName;

    // Follower relations
    @OneToMany(mappedBy = "followee", fetch = FetchType.LAZY, cascade = CascadeType.ALL)
    private Set<UserFollows> followers;

    @OneToMany(mappedBy = "follower", fetch = FetchType.LAZY, cascade = CascadeType.ALL)
    private Set<UserFollows> following;

    // Role relations
    @ManyToMany(fetch = FetchType.EAGER) // many users can have many roles. Eager because there shouldn't be too many roles.
    @JoinTable(
        name = "user_role_junction",
        joinColumns = {@JoinColumn(name = "user_id")},
        inverseJoinColumns = {@JoinColumn(name = "role_id")}
    )
    private Set<Role> authorities;

    // Post relations
    @OneToMany(fetch = FetchType.LAZY)
    private Set<Post> posts;

    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        return this.authorities;
    }
}
