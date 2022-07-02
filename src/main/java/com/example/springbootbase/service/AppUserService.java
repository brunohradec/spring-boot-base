package com.example.springbootbase.service;

import com.example.springbootbase.domain.AppUser;
import com.example.springbootbase.domain.enumeration.AppUserRole;
import com.example.springbootbase.exception.ConflictException;
import com.example.springbootbase.exception.NotFoundException;

import java.util.Optional;

public interface AppUserService {
    AppUser save(AppUser appUser) throws ConflictException;

    Optional<AppUser> find(Long id);

    Optional<AppUser> findByUsername(String username);

    Optional<AppUser> findByEmail(String email);

    AppUser updateByUsername(String username, AppUser updatedAppUser) throws NotFoundException, ConflictException;

    AppUser updatePasswordByUsername(String username, String password) throws NotFoundException;

    AppUser updateRoleByUsername(String username, AppUserRole role) throws NotFoundException;

    void deleteByUsername(String username) throws NotFoundException;
}
