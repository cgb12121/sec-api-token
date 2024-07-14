package com.backend.vertwo.domain.token;


import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

@Builder
@Getter
@Setter
public class Token {
    private String accessToken;
    private String refreshToken;
}
