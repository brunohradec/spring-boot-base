package com.example.springbootbase.controller;

import com.auth0.jwt.exceptions.JWTVerificationException;
import com.auth0.jwt.exceptions.SignatureVerificationException;
import com.auth0.jwt.exceptions.TokenExpiredException;
import com.example.springbootbase.domain.User;
import com.example.springbootbase.dto.AccessTokenDto;
import com.example.springbootbase.dto.UserDto;
import com.example.springbootbase.dto.UserLoginDto;
import com.example.springbootbase.dto.command.RefreshTokenCommand;
import com.example.springbootbase.dto.command.UserLoginCommand;
import com.example.springbootbase.dto.command.UserRegistrationCommand;
import com.example.springbootbase.exception.ConflictException;
import com.example.springbootbase.exception.NotFoundException;
import com.example.springbootbase.mapper.UserMapper;
import com.example.springbootbase.service.AuthService;
import com.example.springbootbase.service.UserService;
import com.example.springbootbase.utility.JwtUtility;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.core.AuthenticationException;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

import java.util.Map;

@RestController
@RequestMapping("/api/auth")
public class AuthController {
    private final AuthService authService;
    private final UserService userService;
    private final UserMapper userMapper;
    private final AuthenticationManager authenticationManager;
    private final JwtUtility jwtUtility;

    public AuthController(
            AuthService authService,
            UserService userService,
            UserMapper userMapper,
            AuthenticationManager authenticationManager,
            JwtUtility jwtUtility) {

        this.authService = authService;
        this.userService = userService;
        this.userMapper = userMapper;
        this.authenticationManager = authenticationManager;
        this.jwtUtility = jwtUtility;
    }

    @PostMapping("/register")
    public ResponseEntity<UserDto> register(@RequestBody UserRegistrationCommand userRegistrationCommand) {
        try {
            User savedUser = userService.save(userMapper.toEntity(userRegistrationCommand));
            return ResponseEntity
                    .status(HttpStatus.CREATED)
                    .body(userMapper.toDto(savedUser));
        } catch (ConflictException exception) {
            throw new ResponseStatusException(
                    HttpStatus.CONFLICT,
                    exception.getMessage(),
                    exception
            );
        }
    }

    @PostMapping("/login")
    public ResponseEntity<UserLoginDto> login(@RequestBody UserLoginCommand userLoginCommand) {
        try {
            Map<String, String> tokens = authService.login(
                    userLoginCommand.getUsername(),
                    userLoginCommand.getPassword()
            );

            UserLoginDto userLoginDto = UserLoginDto.builder()
                    .accessToken(tokens.get("access-token"))
                    .refreshToken(tokens.get("refresh-token"))
                    .build();

            return ResponseEntity
                    .status(HttpStatus.OK)
                    .body(userLoginDto);
        } catch (NotFoundException exception) {
            throw new ResponseStatusException(
                    HttpStatus.NOT_FOUND,
                    exception.getMessage(),
                    exception
            );
        } catch (AuthenticationException exception) {
            throw new ResponseStatusException(
                    HttpStatus.BAD_REQUEST,
                    "Username or password incorrect.",
                    exception
            );
        }
    }

    @GetMapping("/me")
    public ResponseEntity<UserDto> getCurrentlyAuthenticatedUser() {
        return authService
                .getCurrentlyAuthenticatedUser()
                .map(user -> ResponseEntity
                        .status(HttpStatus.OK)
                        .body(userMapper.toDto(user)))
                .orElseThrow(() -> new ResponseStatusException(
                        HttpStatus.NOT_FOUND,
                        "No currently authenticated user.")
                );
    }

    @GetMapping("/refresh-access-token")
    public ResponseEntity<AccessTokenDto> refreshAccessToken(@RequestBody RefreshTokenCommand refreshTokenCommand) {
        try {
            String accessToken = authService.refreshAccessToken(refreshTokenCommand.getRefreshToken());
            return ResponseEntity
                    .status(HttpStatus.OK)
                    .body(new AccessTokenDto(accessToken));
        } catch (SignatureVerificationException exception) {
            throw new ResponseStatusException(
                    HttpStatus.UNAUTHORIZED,
                    "Could not verify access token signature.",
                    exception
            );
        } catch (TokenExpiredException exception) {
            throw new ResponseStatusException(
                    HttpStatus.UNAUTHORIZED,
                    "Access token has timed out.",
                    exception
            );
        } catch (JWTVerificationException exception) {
            throw new ResponseStatusException(
                    HttpStatus.UNAUTHORIZED,
                    "Access token is not valid.",
                    exception
            );
        } catch (NotFoundException exception) {
            throw new ResponseStatusException(
                    HttpStatus.UNAUTHORIZED,
                    exception.getMessage(),
                    exception
            );
        }
    }
}
