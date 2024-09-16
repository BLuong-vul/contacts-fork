package com.vision.middleware.dto;

import com.vision.middleware.domain.ApplicationUser;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
public class LoginResponseDTO {

    // todo: probably want a user DTO instead of this
    private ApplicationUser user;
    private String jwt;

}
