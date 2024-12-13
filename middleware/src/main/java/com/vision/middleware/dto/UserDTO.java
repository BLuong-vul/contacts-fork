package com.vision.middleware.dto;

import lombok.*;
import java.util.Date;

/**
 * Data Transfer Object representing a User.
 * This class encapsulates all necessary information related to a user,
 * including both essential details and customization options visible on the profile.
 */
@Getter
@Setter
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class UserDTO {
    private String username;
    private long userId;
    private String fullName;
    private String email;
    private String phoneNumber;
    private String address;
    private String state;
    private String country;
    private long followerCount;
    private long followingCount;

    // Customization (visible on profile)
    private String displayName;
    private String bio;
    private String occupation;
    private String location;
    private Date birthdate;
    private Date joinDate;

    private String profilePictureFileName;
    private String bannerPictureFileName;
}
