package com.backend.vertwo.service.impl;

import com.backend.vertwo.cache.CacheStore;
import com.backend.vertwo.domain.request.RequestContext;
import com.backend.vertwo.entity.user.*;
import com.backend.vertwo.event.EventType;
import com.backend.vertwo.event.LoginEvent;
import com.backend.vertwo.event.UserEvent;
import com.backend.vertwo.exception.ApiException;
import com.backend.vertwo.repository.ConfirmationUserRepository;
import com.backend.vertwo.repository.CredentialUserRepository;
import com.backend.vertwo.repository.RoleRepository;
import com.backend.vertwo.repository.UserRepository;
import com.backend.vertwo.service.UserService;
import com.backend.vertwo.utils.user.UserUtils;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.Map;
import java.util.Optional;

@Slf4j
@Service
@RequiredArgsConstructor
@Transactional(rollbackFor = Exception.class)
public class UserServiceImpl implements UserService {

    @Autowired
    private final UserRepository userRepository;

    @Autowired
    private final RoleRepository roleRepository;

    @Autowired
    private final ConfirmationUserRepository confirmationUserRepository;

    @Autowired
    private final CredentialUserRepository credentialUserRepository;

    @Autowired
    private final CacheStore<String, Integer> userLoginCache;

//    @Autowired
//    private final BCryptPasswordEncoder bCryptPasswordEncoder;

    @Autowired
    private ApplicationEventPublisher eventPublisher;

    @Override
    public void createUser(String firstName, String lastName, String email, String password) {
        User user = userRepository.save(createNewUser(firstName, lastName, email));
        CredentialUser credentialUser = new CredentialUser(user, password);
        credentialUserRepository.save(credentialUser);
        var confirmationUser = new ConfirmationUser(user);
        eventPublisher.publishEvent(new UserEvent(user, EventType.REGISTRATION, Map.of("key", confirmationUser.getKey())));
    }

    @Override
    public Role getRole(String roleName) {
        Optional<Role> role = roleRepository.findByRoleIgnoreCase(roleName);
        return role.orElseThrow(() -> new ApiException("No such role: " + roleName));
    }

    @Override
    public void verifyAccountKey(String key) {
        ConfirmationUser confirmationUser = getUserConfirmationKey(key);
        User user = getUserConfirmationKey(confirmationUser.getUser().getEmail());
        user.setEnable(true);
        confirmationUserRepository.save(confirmationUser);
        confirmationUserRepository.delete(confirmationUser);
    }

    @Override
    public void updateLoginAttempt(String email, LoginEvent loginEvent) {
        User user = getUserByEmail(email);
        RequestContext.setUserId(user.getId());
        switch (loginEvent) {
            case LOGIN_ATTEMPT -> {
                if (userLoginCache.get(user.getEmail()) == null){
                    userLoginCache.put(user.getEmail(), 1);
                    user.setLoginAttempts(1);
                    user.setAccountNonBlocked(true);
                }

                user.setLoginAttempts(user.getLoginAttempts() + 1);
                userLoginCache.put(user.getEmail(), user.getLoginAttempts());

                if (userLoginCache.get(user.getEmail()) > 5){
                    user.setAccountNonBlocked(false);
                }
            }
            case LOGIN_SUCCESS -> {
                user.setLoginAttempts(0);
                user.setAccountNonBlocked(true);
                user.setLastLogin(LocalDateTime.now());
                userLoginCache.remove(user.getEmail());
            }
        }

        userRepository.save(user);
    }

    @Override
    public User getUserByUserId(String id){
        return userRepository.findById(Long.valueOf(id)).orElseThrow(() -> new ApiException("No such user: " + id));
    }

    @Override
    public User getUserByEmail(String email){
        Optional<User> user = userRepository.findByEmailIgnoreCase(email);
        return user.orElseThrow(() -> new ApiException("No such email: " + email));
    }

    @Override
    public CredentialUser getUserCredentialsById(Long id) {
        return credentialUserRepository.findById(id)
                .orElseThrow(() -> new ApiException("No such user: " + id));
    }

    private ConfirmationUser getUserConfirmationKey(String key) {
        return confirmationUserRepository.findByKey(key)
                .orElseThrow(() -> new ApiException("No such confirmation for user."));
    }

    private User createNewUser(String firstName, String lastName, String email) {
        Role role = getRole(Authority.USER.name());
        return UserUtils.createTempUser(firstName, lastName, email, role);
    }

}
