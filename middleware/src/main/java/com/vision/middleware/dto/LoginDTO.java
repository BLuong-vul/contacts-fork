package com.vision.middleware.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

/**
 * Data Transfer Object for login credentials.
 */
@Getter
@Setter
@AllArgsConstructor
public class LoginDTO {
    private String username;
    private String password;
}
