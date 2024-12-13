package com.vision.middleware.domain;

import com.vision.middleware.domain.relations.UserFollows;
import jakarta.persistence.*;
import lombok.*;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import java.util.Collection;
import java.util.Set;
import java.util.Date;

/**
 * Represents a user in the application with details such as profile information, contact information,
 * roles, and relationships with other users.
 */
@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@Table(name = "application_users")
@Entity
@Builder
public class ApplicationUser implements UserDetails {

    /**
     * Unique identifier for the user.
     */
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "user_id")
    private long id;

    /**
     * File name of the user's profile picture.
     */
    private String profilePictureFileName;

    /**
     * File name of the user's banner picture.
     */
    private String bannerPictureFileName;

    /**
     * Username of the user, must be unique and not null.
     */
    @Column(name = "username", unique = true, nullable=false)
    private String username;

    /**
     * Password of the user, must be not null and have a maximum length of 64 characters.
     */
    @NonNull
    @Column(length = 64, nullable=false)
    private String password;

    /**
     * Full name of the user, must be not null.
     * TODO: Consider removing this field as it might be redundant.
     */
    @NonNull
    private String fullName;

    /**
     * Email of the user, must be unique, not null, and have a maximum length of 127 characters.
     */
    @NonNull
    @Column(unique = true, length = 127, nullable=false)
    private String email;

    /**
     * Phone number of the user, must be unique, not null, and have a maximum length of 15 characters.
     */
    @NonNull
    @Column(unique = true, length = 15)
    private String phoneNumber;

    /**
     * Address of the user.
     * TODO: Consider removing this field as it might be redundant.
     */
    private String address;

    /**
     * City where the user resides.
     * TODO: Consider removing this field as it might be redundant.
     */
    private String city;

    /**
     * State where the user resides.
     * TODO: Consider removing this field as it might be redundant.
     */
    private String state;

    /**
     * Zip code of the user's location.
     * TODO: Consider removing this field as it might be redundant.
     */
    private String zipCode;

    /**
     * Country where the user resides.
     * TODO: Consider removing this field as it might be redundant.
     */
    private String country;

    /**
     * Display name of the user, visible on bio, with a maximum length of 18 characters.
     */
    @Column(length = 18)
    private String displayName; 

    /**
     * Bio of the user, with a maximum length of 255 characters.
     */
    @Column(length = 255)
    private String bio;

    /**
     * Occupation of the user, with a maximum length of 32 characters.
     */
    @Column(length = 32)
    private String occupation;

    /**
     * Location of the user, with a maximum length of 32 characters.
     */
    @Column(length = 32)
    private String location;

    /**
     * Birthdate of the user.
     */
    @Temporal(TemporalType.TIMESTAMP)
    private Date birthdate;

    /**
     * Join date of the user, defaults to the current date and time.
     */
    @Temporal(TemporalType.TIMESTAMP)
    private Date joinDate = new Date();

    /**
     * Number of followers that the user has, defaults to 0.
     */
    @Column(nullable = false)
    private long followerCount = 0;

    /**
     * Number of users that the user is following, defaults to 0.
     */
    @Column(nullable = false)
    private long followingCount = 0;

    /**
     * Set of users who are following this user.
     */
    @OneToMany(mappedBy = "followee", fetch = FetchType.LAZY, cascade = CascadeType.ALL)
    private Set<UserFollows> followers;

    /**
     * Set of users that this user is following.
     */
    @OneToMany(mappedBy = "follower", fetch = FetchType.LAZY, cascade = CascadeType.ALL)
    private Set<UserFollows> following;

    /**
     * Set of roles assigned to the user.
     */
    @ManyToMany(fetch = FetchType.EAGER) // many users can have many roles. Eager because there shouldn't be too many roles.
    @JoinTable(
        name = "user_role_junction",
        joinColumns = {@JoinColumn(name = "user_id")},
        inverseJoinColumns = {@JoinColumn(name = "role_id")}
    )
    private Set<Role> authorities;

    /**
     * Set of posts created by the user.
     */
    @OneToMany(mappedBy = "postedBy", fetch = FetchType.LAZY, cascade = {CascadeType.PERSIST, CascadeType.MERGE})
    private Set<Post> posts;

    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        return this.authorities;
    }
}
