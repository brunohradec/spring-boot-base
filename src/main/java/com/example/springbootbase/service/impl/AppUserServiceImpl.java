package com.example.springbootbase.service.impl;

import com.example.springbootbase.domain.AppUser;
import com.example.springbootbase.domain.enumeration.AppUserRole;
import com.example.springbootbase.exception.ConflictException;
import com.example.springbootbase.exception.NotFoundException;
import com.example.springbootbase.repository.AppUserRepository;
import com.example.springbootbase.service.AppUserService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import javax.transaction.Transactional;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Service
@Transactional
@Slf4j
public class AppUserServiceImpl implements AppUserService, UserDetailsService {
    private final AppUserRepository appUserRepository;
    private final PasswordEncoder passwordEncoder;

    public AppUserServiceImpl(
            AppUserRepository appUserRepository,
            PasswordEncoder passwordEncoder) {

        this.appUserRepository = appUserRepository;
        this.passwordEncoder = passwordEncoder;
    }

    @Override
    public AppUser save(AppUser appUser) throws ConflictException {
        if (appUserRepository.existsByUsername(appUser.getUsername())) {
            String message = "User with the username " + appUser.getUsername() + " already exists.";
            log.error(message);
            throw new ConflictException(message);
        }

        if (appUserRepository.existsByEmail(appUser.getEmail())) {
            String message = "User with the email " + appUser.getEmail() + " already exists.";
            log.error(message);
            throw new ConflictException(message);
        }

        appUser.setRole(AppUserRole.USER);
        appUser.setPassword(passwordEncoder.encode(appUser.getPassword()));

        return appUserRepository.save(appUser);
    }

    @Override
    public Optional<AppUser> find(Long id) {
        return appUserRepository.findById(id);
    }

    @Override
    public Optional<AppUser> findByUsername(String username) {
        return appUserRepository.findByUsername(username);
    }

    @Override
    public Optional<AppUser> findByEmail(String email) {
        return appUserRepository.findByEmail(email);
    }

    @Override
    public AppUser updateByUsername(String username, AppUser updatedAppUser) throws NotFoundException, ConflictException {
        Optional<AppUser> userOptional = appUserRepository.findByUsername(username);

        if (userOptional.isEmpty()) {
            String message = "User with the username " + username + " does not exist";
            log.error(message);
            throw new NotFoundException(message);
        }

        AppUser appUser = userOptional.get();

        if (!updatedAppUser.getUsername().equals(appUser.getUsername())) {
            String message = "User with the username " + appUser.getUsername() + " already exists.";
            log.error(message);
            throw new ConflictException(message);
        }

        if (!updatedAppUser.getEmail().equals(appUser.getEmail())) {
            String message = "User with the email " + appUser.getEmail() + " already exists.";
            log.error(message);
            throw new ConflictException(message);
        }

        updatedAppUser.setId(appUser.getId());
        updatedAppUser.setPassword(appUser.getPassword());
        updatedAppUser.setRole(appUser.getRole());

        return appUserRepository.save(updatedAppUser);
    }

    @Override
    public AppUser updatePasswordByUsername(String username, String password) throws NotFoundException {
        Optional<AppUser> userOptional = appUserRepository.findByUsername(username);

        if (userOptional.isEmpty()) {
            String message = "User with the username " + username + " does not exist";
            log.error(message);
            throw new NotFoundException(message);
        }

        AppUser appUser = userOptional.get();
        appUser.setPassword(passwordEncoder.encode(password));

        return appUserRepository.save(appUser);
    }

    @Override
    public AppUser updateRoleByUsername(String username, AppUserRole role) throws NotFoundException {
        Optional<AppUser> userOptional = appUserRepository.findByUsername(username);

        if (userOptional.isEmpty()) {
            String message = "User with the username " + username + " does not exist";
            log.error(message);
            throw new NotFoundException(message);
        }

        AppUser appUser = userOptional.get();
        appUser.setRole(role);

        return appUserRepository.save(appUser);
    }

    @Override
    public void deleteByUsername(String username) throws NotFoundException {
        if (!appUserRepository.existsByUsername(username)) {
            String message = "User with the id " + username + " does not exist";
            log.error(message);
            throw new NotFoundException(message);
        }

        appUserRepository.deleteByUsername(username);
    }

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        Optional<AppUser> userOptional = appUserRepository.findByUsername(username);

        if (userOptional.isEmpty()) {
            String message = "user with the username " + username + " does not exist.";
            log.error(message);
            throw new UsernameNotFoundException(message);
        }

        AppUser appUser = userOptional.get();

        List<GrantedAuthority> authorities = new ArrayList<>();
        authorities.add(new SimpleGrantedAuthority(appUser.getRole().name()));

        return new User(
                appUser.getUsername(),
                appUser.getPassword(),
                authorities
        );
    }
}
