package com.backend.vertwo.api.controller;

import com.backend.vertwo.domain.request.UserLoginRequest;
import com.backend.vertwo.domain.request.UserRegisterRequest;
import com.backend.vertwo.domain.response.Response;
import com.backend.vertwo.service.UserService;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.net.URI;
import java.util.Map;

import static com.backend.vertwo.utils.request.requestUtils.getResponse;
import static java.util.Collections.emptyMap;

@RestController
@RequiredArgsConstructor
@RequestMapping(path = "/user")
public class UserController {

    @Autowired
    private final UserService userService;

    @Autowired
    private final AuthenticationManager authenticationManager;

    @PostMapping("/login")
    public ResponseEntity<?> login(@RequestBody @Valid UserLoginRequest loginRequest, HttpServletRequest request) {
        UsernamePasswordAuthenticationToken unauthenticated = UsernamePasswordAuthenticationToken.unauthenticated(loginRequest.getEmail(), loginRequest.getPassword());
        Authentication authenticate = authenticationManager.authenticate(unauthenticated);
        return ResponseEntity.ok(Map.of("user", authenticate));
    }

    @PostMapping("/register")
    public ResponseEntity<Response> register(@RequestBody @Valid UserRegisterRequest registerRequest, HttpServletRequest request) {
        userService.createUser(
                registerRequest.getFirstName(),
                registerRequest.getLastName(),
                registerRequest.getEmail(),
                registerRequest.getPassword()
        );
        return ResponseEntity.created(getUri())
                .body(
                        getResponse(
                            request,
                            emptyMap(),
                            "Account created, please check your email for verification.",
                            HttpStatus.CREATED
                        )
                );
    }

    @GetMapping("/verify/account")
    public ResponseEntity<Response> verify(@RequestParam(value = "key") String key, HttpServletRequest request) {
        userService.verifyAccountKey(key);
        return ResponseEntity.ok()
                .body(
                        getResponse(
                            request,
                            emptyMap(),
                            "Account key verified.",
                            HttpStatus.OK
                        )
                );
    }

    private URI getUri() {
        return URI.create("");
    }

}
