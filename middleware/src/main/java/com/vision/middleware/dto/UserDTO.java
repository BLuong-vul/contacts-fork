package com.vision.middleware.dto;

import lombok.*;

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
}
