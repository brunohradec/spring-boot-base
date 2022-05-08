package com.example.springbootbase.service;

import com.example.springbootbase.domain.User;
import com.example.springbootbase.domain.enumeration.UserRole;
import com.example.springbootbase.exception.ConflictException;
import com.example.springbootbase.exception.NotFoundException;

import java.util.Optional;

public interface UserService {
    User save(User user) throws ConflictException;

    Optional<User> findById(Long id);

    Optional<User> findByUsername(String username);

    Optional<User> findByEmail(String email);

    User update(Long id, User updatedUser) throws NotFoundException, ConflictException;

    void updatePassword(Long id, String password) throws NotFoundException;

    void updateRole(Long id, UserRole role) throws NotFoundException;

    void delete(Long id) throws NotFoundException;
}
