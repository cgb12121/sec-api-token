package com.backend.vertwo.entity.user;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

import static com.backend.vertwo.entity.user.AuthorityConstants.*;

@Getter
@RequiredArgsConstructor
public enum Authority {
    USER(USER_AUTHORITY),
    STAFF(STAFF_AUTHORITY),
    MANAGER(MANAGER_AUTHORITY),
    ADMIN(ADMIN_AUTHORITY);

    private final String value;
}
