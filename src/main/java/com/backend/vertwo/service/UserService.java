package com.backend.vertwo.service;

import com.backend.vertwo.entity.user.CredentialUser;
import com.backend.vertwo.entity.user.Role;
import com.backend.vertwo.entity.user.User;
import com.backend.vertwo.event.LoginEvent;


public interface UserService {
    void createUser(String firstName, String lastName, String email, String password);
    Role getRole(String roleName);
    void verifyAccountKey(String token);
    void updateLoginAttempt(String email, LoginEvent loginType);
    User getUserByUserId(String id);
    User getUserByEmail(String email);
    CredentialUser getUserCredentialsById(Long id);
}
