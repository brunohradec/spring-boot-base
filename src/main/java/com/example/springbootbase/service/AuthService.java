package com.example.springbootbase.service;

import com.auth0.jwt.exceptions.JWTVerificationException;
import com.example.springbootbase.domain.AppUser;
import com.example.springbootbase.exception.NotFoundException;
import org.springframework.security.core.AuthenticationException;

import java.util.Map;
import java.util.Optional;

public interface AuthService {
    Map<String, String> login(String username, String password) throws
            NotFoundException,
            AuthenticationException;

    Optional<AppUser> getCurrentlyAuthenticatedUser();

    String refreshAccessToken(String refreshToken) throws JWTVerificationException, NotFoundException;
}
