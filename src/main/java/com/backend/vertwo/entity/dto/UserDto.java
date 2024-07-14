package com.backend.vertwo.entity.dto;

import com.backend.vertwo.entity.user.Role;
import com.backend.vertwo.entity.user.User;
import lombok.Value;

import java.io.Serializable;
import java.time.LocalDateTime;

/**
 * DTO for {@link com.backend.vertwo.entity.user.User}
 */
@Value
public class UserDto implements Serializable {
    Long id;
    String referenceId;
    Long createdBy;
    Long updatedBy;
    LocalDateTime createdAt;
    LocalDateTime updatedAt;
    User owner;
    Long userId;
    String firstName;
    String lastName;
    String email;
    String phone;
    Integer loginAttempts;
    LocalDateTime lastLogin;
    boolean accountNonExpired;
    boolean accountNonBlocked;
    boolean enable;
    boolean multiFactorAuthentication;
    String bio;
    String avatar;
    Role role;
}