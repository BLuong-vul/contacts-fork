package com.vision.middleware.domain;

import com.vision.middleware.domain.relations.UserFollows;
import jakarta.persistence.*;
import lombok.*;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import java.util.Collection;
import java.util.Set;
import java.util.Date;

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

    private String profilePictureFileName;
    // Essential info
    @Column(name = "username", unique = true, nullable=false)
    private String username;

    @NonNull
    @Column(length = 64, nullable=false)
    private String password;
    @NonNull
    private String fullName; // TODO: DELETE

    @NonNull
    @Column(unique = true, length = 127, nullable=false)
    private String email;

    @NonNull
    @Column(unique = true, length = 15)
    private String phoneNumber;
  
    @NonNull private String address; // TODO: DELETE
    @NonNull private String city; // TODO: DELETE
    @NonNull private String state; // TODO: DELETE
    @NonNull private String zipCode; // TODO: DELETE
    @NonNull private String country; // TODO: DELETE

    // Customization (Visible on Bio)
    @Column(length = 18)
    private String displayName; 

    @Column(length = 255)
    private String bio;

    @Column(length = 32)
    private String occupation;

    @Column(length = 32)
    private String location;

    @Temporal(TemporalType.TIMESTAMP)
    private Date birthdate;

    @Temporal(TemporalType.TIMESTAMP)
    private Date joinDate = new Date();

    // Followers / Following
    @Column(nullable=false)
    private long followerCount = 0;
    @Column(nullable=false)
    private long followingCount = 0;

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
