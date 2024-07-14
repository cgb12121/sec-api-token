package com.backend.vertwo.security;

import com.backend.vertwo.domain.user.UserPrincipal;
import com.backend.vertwo.domain.authentication.ApiAuthentication;
import com.backend.vertwo.entity.user.CredentialUser;
import com.backend.vertwo.entity.user.User;
import com.backend.vertwo.exception.ApiException;
import com.backend.vertwo.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.*;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;
import java.util.function.Consumer;
import java.util.function.Function;

@Component
@RequiredArgsConstructor
public class ApiAuthenticationProvider implements AuthenticationProvider {

    private final UserService userService;

    private final BCryptPasswordEncoder bCryptPasswordEncoder;

    private final Function<Authentication, ApiAuthentication> authenticationFunction = authentication -> (ApiAuthentication) authentication;

    private final Consumer<UserPrincipal> validAccount = userPrincipal -> {
        if (!userPrincipal.isAccountNonLocked()) throw new LockedException("Your account is locked");
        if (!userPrincipal.isAccountNonExpired()) throw new AccountExpiredException("Your account is expired");
        if (!userPrincipal.isCredentialsNonExpired()) throw new CredentialsExpiredException("Your credentials are expired");
        if (!userPrincipal.isEnabled()) throw new DisabledException("Your account is disabled");
    };

    @Override
    public Authentication authenticate(Authentication authentication) throws AuthenticationException {
        ApiAuthentication apiAuthentication = authenticationFunction.apply(authentication);
        User user = userService.getUserByEmail(apiAuthentication.getEmail());

        if (user != null) {
            CredentialUser userCredentials = userService.getUserCredentialsById(user.getUserId());
            if (userCredentials.getUpdatedAt().plusDays(90).isBefore(LocalDateTime.now())) {
                throw new BadCredentialsException("Credentials are expired.");
            }
            var userPrinciples = new UserPrincipal(user, userCredentials);
            validAccount.accept(userPrinciples);
            if (bCryptPasswordEncoder.matches(apiAuthentication.getPassword(), userCredentials.getPassword())) {
                return ApiAuthentication.authenticated(user, userPrinciples.getAuthorities());
            } else {
                throw new BadCredentialsException("Please try again.");
            }
        } else {
            throw new ApiException("Unable to authenticate user.");
        }
    }

    @Override
    public boolean supports(Class<?> authentication) {
        return ApiAuthentication.class.isAssignableFrom(authentication);
    }

}
