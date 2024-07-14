package com.backend.vertwo.utils.user;

import com.backend.vertwo.entity.user.Role;
import com.backend.vertwo.entity.user.User;

import java.time.LocalDateTime;
import java.util.UUID;

import static org.apache.commons.lang3.StringUtils.EMPTY;

public class UserUtils {

    public static User createTempUser(String firstName, String lastName, String email, Role role) {
        return User.builder()
                .userId(Long.valueOf(UUID.randomUUID().toString()))
                .firstName(firstName)
                .lastName(lastName)
                .email(email)
                .phone(EMPTY)
                .loginAttempts(0)
                .lastLogin(LocalDateTime.now())
                .accountNonExpired(true)
                .accountNonBlocked(true)
                .bio(EMPTY)
                .avatar("https://www.svgrepo.com/show/13644/avatar.svg")
                .enable(false)
                .multiFactorAuthentication(false)
                .role(role)
                .build();
    }

}
