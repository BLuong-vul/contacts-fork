package com.vision.middleware.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
public class RegistrationDTO {
    private String username;
    private String password;

    public String toString() {
        return "Registration info | username: " + this.username + " password: " + this.password; //todo
    }
}
