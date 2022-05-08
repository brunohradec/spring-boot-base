package com.example.springbootbase.service.impl;

import com.example.springbootbase.domain.User;
import com.example.springbootbase.domain.enumeration.UserRole;
import com.example.springbootbase.exception.ConflictException;
import com.example.springbootbase.exception.NotFoundException;
import com.example.springbootbase.repository.UserRepository;
import com.example.springbootbase.service.UserService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
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
public class UserServiceImpl implements UserService, UserDetailsService {
    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    public UserServiceImpl(
            UserRepository userRepository,
            PasswordEncoder passwordEncoder) {

        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
    }

    @Override
    public User save(User user) throws ConflictException {
        if (userRepository.existsByUsername(user.getUsername())) {
            String message = "User with the username " + user.getUsername() + " already exists.";
            log.error(message);
            throw new ConflictException(message);
        }

        if (userRepository.existsByEmail(user.getEmail())) {
            String message = "User with the email " + user.getEmail() + " already exists.";
            log.error(message);
            throw new ConflictException(message);
        }

        user.setRole(UserRole.USER);
        user.setPassword(passwordEncoder.encode(user.getPassword()));

        return userRepository.save(user);
    }

    @Override
    public Optional<User> find(Long id) {
        return userRepository.findById(id);
    }

    @Override
    public Optional<User> findByUsername(String username) {
        return userRepository.findByUsername(username);
    }

    @Override
    public Optional<User> findByEmail(String email) {
        return userRepository.findByEmail(email);
    }

    @Override
    public User updateByUsername(String username, User updatedUser) throws NotFoundException, ConflictException {
        Optional<User> userOptional = userRepository.findByUsername(username);

        if (userOptional.isEmpty()) {
            String message = "User with the username " + username + " does not exist";
            log.error(message);
            throw new NotFoundException(message);
        }

        User user = userOptional.get();

        if (!updatedUser.getUsername().equals(user.getUsername())) {
            String message = "User with the username " + user.getUsername() + " already exists.";
            log.error(message);
            throw new ConflictException(message);
        }

        if (!updatedUser.getEmail().equals(user.getEmail())) {
            String message = "User with the email " + user.getEmail() + " already exists.";
            log.error(message);
            throw new ConflictException(message);
        }

        updatedUser.setId(user.getId());
        updatedUser.setPassword(user.getPassword());
        updatedUser.setRole(user.getRole());

        return userRepository.save(updatedUser);
    }

    @Override
    public User updatePasswordByUsername(String username, String password) throws NotFoundException {
        Optional<User> userOptional = userRepository.findByUsername(username);

        if (userOptional.isEmpty()) {
            String message = "User with the username " + username + " does not exist";
            log.error(message);
            throw new NotFoundException(message);
        }

        User user = userOptional.get();
        user.setPassword(passwordEncoder.encode(password));

        return userRepository.save(user);
    }

    @Override
    public User updateRoleByUsername(String username, UserRole role) throws NotFoundException {
        Optional<User> userOptional = userRepository.findByUsername(username);

        if (userOptional.isEmpty()) {
            String message = "User with the username " + username + " does not exist";
            log.error(message);
            throw new NotFoundException(message);
        }

        User user = userOptional.get();
        user.setRole(role);

        return userRepository.save(user);
    }

    @Override
    public void deleteByUsername(String username) throws NotFoundException {
        if (!userRepository.existsByUsername(username)) {
            String message = "User with the id " + username + " does not exist";
            log.error(message);
            throw new NotFoundException(message);
        }

        userRepository.deleteByUsername(username);
    }

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        Optional<User> userOptional = userRepository.findByUsername(username);

        if (userOptional.isEmpty()) {
            String message = "user with the username " + username + " does not exist.";
            log.error(message);
            throw new UsernameNotFoundException(message);
        }

        User user = userOptional.get();

        List<GrantedAuthority> authorities = new ArrayList<>();
        authorities.add(new SimpleGrantedAuthority(user.getRole().name()));

        return new org.springframework.security.core.userdetails.User(
                user.getUsername(),
                user.getPassword(),
                authorities
        );
    }
}
