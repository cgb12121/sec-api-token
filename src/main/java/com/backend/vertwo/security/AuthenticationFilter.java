package com.backend.vertwo.security;

import com.backend.vertwo.domain.authentication.ApiAuthentication;
import com.backend.vertwo.domain.request.UserLoginRequest;
import com.backend.vertwo.domain.token.TokenType;
import com.backend.vertwo.entity.user.User;
import com.backend.vertwo.event.LoginEvent;
import com.backend.vertwo.service.JwtService;
import com.backend.vertwo.service.UserService;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.ServletOutputStream;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.AuthenticationServiceException;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.authentication.AbstractAuthenticationProcessingFilter;
import org.springframework.security.web.util.matcher.AntPathRequestMatcher;

import java.io.IOException;
import java.util.Map;

import static com.backend.vertwo.utils.request.requestUtils.getResponse;
import static com.backend.vertwo.utils.request.requestUtils.handleErrorResponse;
import static com.fasterxml.jackson.core.JsonParser.Feature.AUTO_CLOSE_SOURCE;
import static org.springframework.http.HttpMethod.POST;
import static org.springframework.http.HttpStatus.OK;

@Slf4j
public class AuthenticationFilter extends AbstractAuthenticationProcessingFilter {

    private final UserService userService;

    private final JwtService jwtService;

    protected AuthenticationFilter(AuthenticationManager authenticationManager, UserService userService, JwtService jwtService) {
        super(new AntPathRequestMatcher("/user/login", POST.name()),authenticationManager);
        this.userService = userService;
        this.jwtService = jwtService;
    }

    @Override
    public Authentication attemptAuthentication(HttpServletRequest request, HttpServletResponse response) throws AuthenticationException {
        try {
            UserLoginRequest user = new ObjectMapper().configure(AUTO_CLOSE_SOURCE, true)
                    .readValue(request.getInputStream(), UserLoginRequest.class);
            userService.updateLoginAttempt(user.getEmail(), LoginEvent.LOGIN_ATTEMPT);

            ApiAuthentication authentication = ApiAuthentication.unAuthenticated(user.getEmail(), user.getPassword());
            return getAuthenticationManager().authenticate(authentication);
        } catch (Exception exception) {
            log.error(exception.getMessage());
            handleErrorResponse(request, response, exception);
            throw new AuthenticationServiceException(exception.getMessage(), exception);
        }
    }

    @Override
    protected void successfulAuthentication(HttpServletRequest request, HttpServletResponse response, FilterChain chain, Authentication authResult) throws IOException, ServletException {
        User user = (User) authResult.getPrincipal();
        userService.updateLoginAttempt(user.getEmail(), LoginEvent.LOGIN_SUCCESS);
        boolean httpResponse = user.isMultiFactorAuthentication();
        if (!httpResponse) {
            sendRespond(request, response, user);
        }
        response.setStatus(OK.value());
        ServletOutputStream out = response.getOutputStream();
        ObjectMapper mapper = new ObjectMapper();
        mapper.writeValue(out, httpResponse);
        out.flush();
    }

    private void sendRespond(HttpServletRequest request, HttpServletResponse response, User user) {
        jwtService.addCookie(response, user, TokenType.ACCESS);
        jwtService.addCookie(response, user, TokenType.REFRESH);
        getResponse(request, Map.of("user", user), "Login success", OK);
    }
}
