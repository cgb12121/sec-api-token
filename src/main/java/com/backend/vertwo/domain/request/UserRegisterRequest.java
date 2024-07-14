package com.backend.vertwo.domain.request;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@JsonIgnoreProperties(ignoreUnknown = true)
public class UserRegisterRequest {
    private String firstName;
    private String lastName;
    private String email;
    private String password;
}

