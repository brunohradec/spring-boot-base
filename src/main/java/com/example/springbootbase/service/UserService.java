package com.example.springbootbase.service;

import com.example.springbootbase.domain.User;
import com.example.springbootbase.domain.enumeration.UserRole;
import com.example.springbootbase.exception.ConflictException;
import com.example.springbootbase.exception.NotFoundException;

import java.util.Optional;

public interface UserService {
    User save(User user) throws ConflictException;

    Optional<User> find(Long id);

    Optional<User> findByUsername(String username);

    Optional<User> findByEmail(String email);

    User updateByUsername(String username, User updatedUser) throws NotFoundException, ConflictException;

    User updatePasswordByUsername(String username, String password) throws NotFoundException;

    User updateRoleByUsername(String username, UserRole role) throws NotFoundException;

    void deleteByUsername(String username) throws NotFoundException;
}
