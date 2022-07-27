package com.example.springbootbase.controller;

import com.auth0.jwt.exceptions.JWTVerificationException;
import com.auth0.jwt.exceptions.SignatureVerificationException;
import com.auth0.jwt.exceptions.TokenExpiredException;
import com.example.springbootbase.domain.AppUser;
import com.example.springbootbase.dto.AccessTokenDto;
import com.example.springbootbase.dto.AppUserDto;
import com.example.springbootbase.dto.AppUserLoginDto;
import com.example.springbootbase.dto.command.AppUserLoginCommand;
import com.example.springbootbase.dto.command.AppUserRegistrationCommand;
import com.example.springbootbase.dto.command.RefreshTokenCommand;
import com.example.springbootbase.exception.ConflictException;
import com.example.springbootbase.exception.NotFoundException;
import com.example.springbootbase.mapper.AppUserMapper;
import com.example.springbootbase.service.AppUserService;
import com.example.springbootbase.service.AuthService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.AuthenticationException;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

import javax.validation.Valid;
import java.util.Map;

@RestController
@RequestMapping("/api/auth")
public class AuthController {
    private final AuthService authService;
    private final AppUserService appUserService;
    private final AppUserMapper appUserMapper;

    public AuthController(
            AuthService authService,
            AppUserService appUserService,
            AppUserMapper appUserMapper) {

        this.authService = authService;
        this.appUserService = appUserService;
        this.appUserMapper = appUserMapper;
    }

    @PostMapping("/register")
    public ResponseEntity<AppUserDto> register(@Valid @RequestBody AppUserRegistrationCommand appUserRegistrationCommand) {
        try {
            AppUser savedAppUser = appUserService.save(appUserMapper.toEntity(appUserRegistrationCommand));
            return ResponseEntity
                    .status(HttpStatus.CREATED)
                    .body(appUserMapper.toDto(savedAppUser));
        } catch (ConflictException exception) {
            throw new ResponseStatusException(
                    HttpStatus.CONFLICT,
                    exception.getMessage(),
                    exception
            );
        }
    }

    @PostMapping("/login")
    public ResponseEntity<AppUserLoginDto> login(@Valid @RequestBody AppUserLoginCommand appUserLoginCommand) {
        try {
            Map<String, String> tokens = authService.login(
                    appUserLoginCommand.getUsername(),
                    appUserLoginCommand.getPassword()
            );

            AppUserLoginDto appUserLoginDto = AppUserLoginDto.builder()
                    .accessToken(tokens.get("access-token"))
                    .refreshToken(tokens.get("refresh-token"))
                    .build();

            return ResponseEntity
                    .status(HttpStatus.OK)
                    .body(appUserLoginDto);
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
    public ResponseEntity<AppUserDto> getCurrentlyAuthenticatedUser() {
        return authService
                .getCurrentlyAuthenticatedUser()
                .map(user -> ResponseEntity
                        .status(HttpStatus.OK)
                        .body(appUserMapper.toDto(user)))
                .orElseThrow(() -> new ResponseStatusException(
                        HttpStatus.NOT_FOUND,
                        "No currently authenticated user.")
                );
    }

    @GetMapping("/refresh-access-token")
    public ResponseEntity<AccessTokenDto> refreshAccessToken(@Valid @RequestBody RefreshTokenCommand refreshTokenCommand) {
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
