package com.backend.vertwo.domain.token;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum TokenType {
    ACCESS("access-token"),
    REFRESH("refresh-token"),;

    private final String value;

}
