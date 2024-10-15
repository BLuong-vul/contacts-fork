package com.vision.middleware.dto;

import jakarta.persistence.Column;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
@Builder
public class RegistrationDTO {
    private String username;
    private String password;

    private String fullName;
    private String email;
    private String phoneNumber;
    private String address;
    private String city;
    private String state;
    private String zipCode;
    private String country;

}
