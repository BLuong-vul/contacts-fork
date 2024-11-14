package com.vision.middleware.dto;

import lombok.*;
import java.util.Date;

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
}
