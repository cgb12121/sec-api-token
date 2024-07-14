package com.backend.vertwo.service.impl;

import com.backend.vertwo.domain.token.Token;
import com.backend.vertwo.domain.token.TokenData;
import com.backend.vertwo.domain.token.TokenType;
import com.backend.vertwo.entity.user.User;
import com.backend.vertwo.security.JwtConfiguration;
import com.backend.vertwo.service.JwtService;
import com.backend.vertwo.service.UserService;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.JwtBuilder;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.security.Keys;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.function.TriConsumer;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.stereotype.Service;

import javax.crypto.SecretKey;
import java.time.Instant;
import java.util.*;
import java.util.function.BiFunction;
import java.util.function.Function;
import java.util.function.Supplier;
import java.util.stream.Stream;

import static com.backend.vertwo.domain.token.TokenType.ACCESS;
import static com.backend.vertwo.domain.token.TokenType.REFRESH;
import static com.backend.vertwo.entity.user.AuthorityConstants.*;
import static org.springframework.security.core.authority.AuthorityUtils.commaSeparatedStringToAuthorityList;

@Slf4j
@Service
@RequiredArgsConstructor
public class JwtServiceImpl extends JwtConfiguration implements JwtService {

    @Autowired
    private final UserService userService;

    private final Supplier<SecretKey> key = () -> Keys.hmacShaKeyFor(Decoders.BASE64.decode(getSecret()));

    private final Function<String, Claims> claimsFunction = token ->
            Jwts.parser()
                    .verifyWith(key.get())
                    .build()
                    .parseSignedClaims(token)
                    .getPayload();

    private final Function<String, String> subject = token ->
            getClaimsValue(token, Claims::getSubject);

    private final BiFunction<HttpServletRequest, String, Optional<String>> extractToken = (request, cookieName) -> {
        if (request.getCookies() == null) {
            return Optional.empty();
        }

        return Stream.of(request.getCookies())
                .filter(cookie -> Objects.equals(cookieName, cookie.getName()))
                .map(Cookie::getValue)
                .findAny();
    };

    private final BiFunction<HttpServletRequest, String, Optional<Cookie>> extractCookie = (request, cookieName) -> {
        if (request.getCookies() == null) {
            return Optional.empty();
        }

        return Stream.of(request.getCookies())
                .filter(cookie -> Objects.equals(cookieName, cookie.getName()))
                .findAny();
    };

    private final Supplier<JwtBuilder> builder = () ->
            Jwts.builder()
                .header().add(Map.of("typ", "JWT"))
                .and()
                .audience().add("GET_ARRAYS_LLC")
                .and()
                .id(UUID.randomUUID().toString())
                .issuedAt(Date.from(Instant.now()))
                .notBefore(new Date())
                .signWith(key.get(), Jwts.SIG.HS512);

    private <T> T getClaimsValue(String token, Function<Claims, T> claims) {
        return claimsFunction.andThen(claims).apply(token);
    }

    private final BiFunction<User, TokenType, String> buildToken = (user, tokenType) ->
            Objects.equals(tokenType, ACCESS) ? builder.get()
                    .subject(String.valueOf(user.getUserId()))
                    .claim(AUTHORITIES, user.getAuthorities())
                    .claim(ROLE, user.getRole())
                    .expiration(Date.from(Instant.now().plusSeconds(Long.parseLong(getExpiration()))))
                    .compact() : builder.get()
                    .subject(String.valueOf(user.getUserId()))
                    .expiration(Date.from(Instant.now().plusSeconds(Long.parseLong(getExpiration()))))
                    .compact();

    private final TriConsumer<HttpServletResponse, User, TokenType> addCookie = (response, user, tokenType) -> {
        switch (tokenType) {
            case ACCESS -> {
                String accessToken = createToken(user, Token::getAccessToken);
                Cookie cookie = new Cookie("access_token", accessToken);
                cookie.setHttpOnly(true);
                cookie.setSecure(true);
                cookie.setMaxAge(2 * 60);
                cookie.setPath("/");
                cookie.setAttribute("SameSite", "None");
                response.addCookie(cookie);
            }

            case REFRESH -> {
                String refreshToken = createToken(user, Token::getAccessToken);
                Cookie cookie = new Cookie("access_token", refreshToken);
                cookie.setHttpOnly(true);
                cookie.setSecure(true);
                cookie.setMaxAge(2 * 60 * 60);
                cookie.setPath("/");
                cookie.setAttribute("SameSite", "None");
                response.addCookie(cookie);
            }
        }
    };

    public Function<String, List<GrantedAuthority>> authority = token ->
            commaSeparatedStringToAuthorityList(
                    new StringJoiner(AUTHORITY_DELIMITER)
                            .add(claimsFunction
                                    .apply(token)
                                    .get(AUTHORITIES, String.class)
                            ).add(ROLE_PREFIX + claimsFunction
                                    .apply(token)
                                    .get(ROLE, String.class))
                    .toString()
            );

    @Override
    public String createToken(User user, Function<Token, String> tokenFunction) {
        Token token = Token.builder()
                .accessToken(buildToken.apply(user, ACCESS))
                .refreshToken(buildToken.apply(user, REFRESH))
                .build();
        return tokenFunction.apply(token);
    }

    @Override
    public Optional<String> extractToken(HttpServletRequest request, String cookieName) {
        return extractToken.apply(request, cookieName);
    }

    @Override
    public void addCookie(HttpServletResponse response, User user, TokenType tokenType) {
        addCookie.accept(response, user, tokenType);
    }

    @Override
    public <T> T getTokenData(String token, Function<TokenData, T> tokenFunction) {
        return tokenFunction.apply(TokenData.builder()
                        .isValid(Objects.equals(String.valueOf(userService.getUserByUserId(subject.apply(token)).getUserId()),
                                                claimsFunction.apply(token).getSubject()))
                        .authorities(authority.apply(token))
                        .user(userService.getUserByUserId(subject.apply(token)))
                        .build()
        );
    }

    @Override
    public void removeCookie(HttpServletRequest request, HttpServletResponse response, String cookieName) {
        var optionalCookie = extractCookie.apply(request, cookieName);
        if (optionalCookie.isPresent()) {
            var cookie = optionalCookie.get();
            cookie.setMaxAge(0);
            response.addCookie(cookie);
        }
    }
}
