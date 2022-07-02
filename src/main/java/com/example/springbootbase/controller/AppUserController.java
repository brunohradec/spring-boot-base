package com.example.springbootbase.controller;

import com.example.springbootbase.domain.AppUser;
import com.example.springbootbase.dto.AppUserDto;
import com.example.springbootbase.dto.command.AppUserUpdateCommand;
import com.example.springbootbase.dto.command.AppUserUpdatePasswordCommand;
import com.example.springbootbase.dto.command.UserUpdateRoleCommand;
import com.example.springbootbase.exception.ConflictException;
import com.example.springbootbase.exception.NotFoundException;
import com.example.springbootbase.mapper.AppUserMapper;
import com.example.springbootbase.service.AppUserService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

import javax.annotation.security.RolesAllowed;
import javax.validation.Valid;

@RestController
@RequestMapping("/api/users")
public class AppUserController {
    private final AppUserService appUserService;
    private final AppUserMapper appUserMapper;

    public AppUserController(
            AppUserService appUserService,
            AppUserMapper appUserMapper) {

        this.appUserService = appUserService;
        this.appUserMapper = appUserMapper;
    }

    @GetMapping("/{id}")
    public ResponseEntity<AppUserDto> find(@PathVariable Long id) {
        return appUserService
                .find(id)
                .map(user -> ResponseEntity
                        .status(HttpStatus.OK)
                        .body(appUserMapper.toDto(user)))
                .orElseThrow(() -> new ResponseStatusException(
                        HttpStatus.NOT_FOUND,
                        "User with the id " + id + " could not be found.")
                );
    }

    @GetMapping(path = "/", params = "username")
    public ResponseEntity<AppUserDto> findByUsername(@RequestParam String username) {
        return appUserService
                .findByUsername(username)
                .map(user -> ResponseEntity
                        .status(HttpStatus.OK)
                        .body(appUserMapper.toDto(user)))
                .orElseThrow(() -> new ResponseStatusException(
                        HttpStatus.NOT_FOUND,
                        "User with the username " + username + " could not be found.")
                );
    }

    @GetMapping(path = "/", params = "email")
    public ResponseEntity<AppUserDto> findByEmail(@RequestParam String email) {
        return appUserService
                .findByEmail(email)
                .map(user -> ResponseEntity
                        .status(HttpStatus.OK)
                        .body(appUserMapper.toDto(user)))
                .orElseThrow(() -> new ResponseStatusException(
                        HttpStatus.NOT_FOUND,
                        "User with the email " + email + " could not be found.")
                );
    }

    @PutMapping("/{username}")
    public ResponseEntity<AppUserDto> update(
            @PathVariable String username,
            @Valid @RequestBody AppUserUpdateCommand appUserUpdateCommand) {

        try {
            AppUser updatedAppUser = appUserService.updateByUsername(username, appUserMapper.toEntity(appUserUpdateCommand));
            return ResponseEntity
                    .status(HttpStatus.OK)
                    .body(appUserMapper.toDto(updatedAppUser));
        } catch (NotFoundException exception) {
            throw new ResponseStatusException(
                    HttpStatus.NOT_FOUND,
                    exception.getMessage(),
                    exception
            );
        } catch (ConflictException exception) {
            throw new ResponseStatusException(
                    HttpStatus.CONFLICT,
                    exception.getMessage(),
                    exception
            );
        }
    }

    @PutMapping("/password")
    public ResponseEntity<AppUserDto> updatePasswordForCurrentUser(
            @Valid @RequestBody AppUserUpdatePasswordCommand appUserUpdatePasswordCommand) {

        try {
            UserDetails currentlySignedInUser = (UserDetails) SecurityContextHolder
                    .getContext()
                    .getAuthentication()
                    .getPrincipal();

            AppUser updatedAppUser = appUserService.updatePasswordByUsername(
                    currentlySignedInUser.getUsername(),
                    appUserUpdatePasswordCommand.getPassword()
            );

            return ResponseEntity
                    .status(HttpStatus.OK)
                    .body(appUserMapper.toDto(updatedAppUser));
        } catch (NotFoundException exception) {
            throw new ResponseStatusException(
                    HttpStatus.NOT_FOUND,
                    exception.getMessage(),
                    exception
            );
        }
    }

    @PutMapping("/{username}/password")
    public ResponseEntity<AppUserDto> updatePassword(
            @PathVariable String username,
            @RequestBody AppUserUpdatePasswordCommand appUserUpdatePasswordCommand) {

        try {
            AppUser updatedAppUser = appUserService.updatePasswordByUsername(username, appUserUpdatePasswordCommand.getPassword());
            return ResponseEntity
                    .status(HttpStatus.OK)
                    .body(appUserMapper.toDto(updatedAppUser));
        } catch (NotFoundException exception) {
            throw new ResponseStatusException(
                    HttpStatus.NOT_FOUND,
                    exception.getMessage(),
                    exception
            );
        }
    }

    @PutMapping("/{username}/role")
    public ResponseEntity<AppUserDto> updateRole(
            @PathVariable String username,
            @RequestBody UserUpdateRoleCommand userUpdateRoleCommand) {

        try {
            AppUser updatedAppUser = appUserService.updateRoleByUsername(username, userUpdateRoleCommand.getRole());
            return ResponseEntity
                    .status(HttpStatus.OK)
                    .body(appUserMapper.toDto(updatedAppUser));
        } catch (NotFoundException exception) {
            throw new ResponseStatusException(
                    HttpStatus.NOT_FOUND,
                    exception.getMessage(),
                    exception
            );
        }
    }

    @DeleteMapping("/{username}")
    @RolesAllowed("ROLE_ADMIN")
    public ResponseEntity<Void> deleteByUsername(@PathVariable String username) {
        try {
            appUserService.deleteByUsername(username);
            return ResponseEntity
                    .status(HttpStatus.NO_CONTENT)
                    .build();
        } catch (NotFoundException exception) {
            throw new ResponseStatusException(
                    HttpStatus.NOT_FOUND,
                    exception.getMessage(),
                    exception
            );
        }
    }
}
