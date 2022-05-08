package com.example.springbootbase.controller;

import com.example.springbootbase.domain.User;
import com.example.springbootbase.dto.UserDto;
import com.example.springbootbase.dto.command.UserUpdateCommand;
import com.example.springbootbase.dto.command.UserUpdatePasswordCommand;
import com.example.springbootbase.dto.command.UserUpdateRoleCommand;
import com.example.springbootbase.exception.ConflictException;
import com.example.springbootbase.exception.NotFoundException;
import com.example.springbootbase.mapper.UserMapper;
import com.example.springbootbase.service.UserService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

import javax.validation.Valid;

@RestController
@RequestMapping("/api/users")
public class UserController {
    private final UserService userService;
    private final UserMapper userMapper;

    public UserController(
            UserService userService,
            UserMapper userMapper) {

        this.userService = userService;
        this.userMapper = userMapper;
    }

    @GetMapping("/{id}")
    public ResponseEntity<UserDto> find(@PathVariable Long id) {
        return userService
                .find(id)
                .map(user -> ResponseEntity.status(HttpStatus.OK).body(userMapper.toDto(user)))
                .orElseThrow(() -> new ResponseStatusException(
                        HttpStatus.NOT_FOUND,
                        "User with the id " + id + " could not be found.")
                );
    }

    @GetMapping(path = "/", params = "username")
    public ResponseEntity<UserDto> findByUsername(@RequestParam String username) {
        return userService
                .findByUsername(username)
                .map(user -> ResponseEntity.status(HttpStatus.OK).body(userMapper.toDto(user)))
                .orElseThrow(() -> new ResponseStatusException(
                        HttpStatus.NOT_FOUND,
                        "User with the username " + username + " could not be found.")
                );
    }

    @GetMapping(path = "/", params = "email")
    public ResponseEntity<UserDto> findByEmail(@RequestParam String email) {
        return userService
                .findByEmail(email)
                .map(user -> ResponseEntity.status(HttpStatus.OK).body(userMapper.toDto(user)))
                .orElseThrow(() -> new ResponseStatusException(
                        HttpStatus.NOT_FOUND,
                        "User with the email " + email + " could not be found.")
                );
    }

    @PutMapping("/{username}")
    public ResponseEntity<UserDto> update(
            @PathVariable String username,
            @Valid @RequestBody UserUpdateCommand userUpdateCommand) {

        try {
            User updatedUser = userService.updateByUsername(username, userMapper.toEntity(userUpdateCommand));
            return ResponseEntity.status(HttpStatus.OK).body(userMapper.toDto(updatedUser));
        } catch (NotFoundException exception) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, exception.getMessage());
        } catch (ConflictException exception) {
            throw new ResponseStatusException(HttpStatus.CONFLICT, exception.getMessage());
        }
    }

    @PutMapping("/password")
    public ResponseEntity<UserDto> updatePasswordForCurrentUser(
            @Valid @RequestBody UserUpdatePasswordCommand userUpdatePasswordCommand) {

        try {
            UserDetails currentlySignedInUser = (UserDetails) SecurityContextHolder
                    .getContext()
                    .getAuthentication()
                    .getPrincipal();

            User updatedUser = userService.updatePasswordByUsername(
                    currentlySignedInUser.getUsername(),
                    userUpdatePasswordCommand.getPassword()
            );

            return ResponseEntity.status(HttpStatus.OK).body(userMapper.toDto(updatedUser));
        } catch (NotFoundException exception) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, exception.getMessage());
        }
    }

    @PutMapping("/{username}/password")
    public ResponseEntity<UserDto> updatePassword(
            @PathVariable String username,
            @RequestBody UserUpdatePasswordCommand userUpdatePasswordCommand) {

        try {
            User updatedUser = userService.updatePasswordByUsername(username, userUpdatePasswordCommand.getPassword());
            return ResponseEntity.status(HttpStatus.OK).body(userMapper.toDto(updatedUser));
        } catch (NotFoundException exception) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, exception.getMessage());
        }
    }

    @PutMapping("/{username}/role")
    public ResponseEntity<UserDto> updateRole(
            @PathVariable String username,
            @RequestBody UserUpdateRoleCommand userUpdateRoleCommand) {

        try {
            User updatedUser = userService.updateRoleByUsername(username, userUpdateRoleCommand.getRole());
            return ResponseEntity.status(HttpStatus.OK).body(userMapper.toDto(updatedUser));
        } catch (NotFoundException exception) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, exception.getMessage());
        }
    }

    @DeleteMapping("/{username}")
    public ResponseEntity<Void> deleteByUsername(@PathVariable String username) {
        try {
            userService.deleteByUsername(username);
            return ResponseEntity.status(HttpStatus.NO_CONTENT).build();
        } catch (NotFoundException exception) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, exception.getMessage());
        }
    }
}
