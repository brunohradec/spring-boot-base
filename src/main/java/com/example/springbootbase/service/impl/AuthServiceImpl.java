package com.example.springbootbase.service.impl;

import com.auth0.jwt.exceptions.SignatureVerificationException;
import com.auth0.jwt.exceptions.TokenExpiredException;
import com.example.springbootbase.domain.AppUser;
import com.example.springbootbase.exception.NotFoundException;
import com.example.springbootbase.service.AppUserService;
import com.example.springbootbase.service.AuthService;
import com.example.springbootbase.utility.JwtUtility;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

@Service
@Slf4j
public class AuthServiceImpl implements AuthService {
    private final AppUserService appUserService;
    private final AuthenticationManager authenticationManager;
    private final JwtUtility jwtUtility;

    public AuthServiceImpl(
            AppUserService appUserService,
            AuthenticationManager authenticationManager,
            JwtUtility jwtUtility) {

        this.appUserService = appUserService;
        this.authenticationManager = authenticationManager;
        this.jwtUtility = jwtUtility;
    }

    @Override
    public Map<String, String> login(String username, String password) throws
            NotFoundException,
            AuthenticationException {

        log.info("Logging in user with the username {}.", username);

        Optional<AppUser> userOptional = appUserService.findByUsername(username);

        if (userOptional.isEmpty()) {
            String message = "User with the username " + username + " does not exist.";
            log.info(message);
            throw new NotFoundException(message);
        }

        AppUser appUser = userOptional.get();

        UsernamePasswordAuthenticationToken authenticationToken = new UsernamePasswordAuthenticationToken(
                username,
                password
        );

        // Authentication exception will be thrown if authentication was not successful.
        authenticationManager.authenticate(authenticationToken);

        // If this point is reached, authentication with username and password was successful.
        log.info(
                "Generating access and refresh tokens for the user with the username {}.",
                username
        );

        String accessToken = jwtUtility.generateAccessToken(appUser);
        String refreshToken = jwtUtility.generateRefreshToken(appUser);

        Map<String, String> tokens = new HashMap<>();

        tokens.put("access-token", accessToken);
        tokens.put("refresh-token", refreshToken);

        return tokens;
    }

    @Override
    public Optional<AppUser> getCurrentlyAuthenticatedUser() {
        String principalUsername = (String) SecurityContextHolder
                .getContext()
                .getAuthentication()
                .getPrincipal();

        return appUserService.findByUsername(principalUsername);
    }

    @Override
    public String refreshAccessToken(String refreshToken) throws
            SignatureVerificationException,
            TokenExpiredException,
            NotFoundException {

        String username = jwtUtility.validateRefreshTokenAndRetrieveSubject(refreshToken);
        Optional<AppUser> userOptional = appUserService.findByUsername(username);

        if (userOptional.isEmpty()) {
            String message = "User with the username " + username + " does not exist.";
            log.info(message);
            throw new NotFoundException(message);
        }

        AppUser appUser = userOptional.get();

        return jwtUtility.generateAccessToken(appUser);
    }
}
