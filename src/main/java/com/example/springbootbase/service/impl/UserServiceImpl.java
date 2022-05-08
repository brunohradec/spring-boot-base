package com.example.springbootbase.service.impl;

import com.example.springbootbase.domain.User;
import com.example.springbootbase.domain.enumeration.UserRole;
import com.example.springbootbase.exception.ConflictException;
import com.example.springbootbase.exception.NotFoundException;
import com.example.springbootbase.repository.UserRepository;
import com.example.springbootbase.service.UserService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import javax.transaction.Transactional;
import java.util.Optional;

@Service
@Transactional
@Slf4j
public class UserServiceImpl implements UserService {
    private final UserRepository userRepository;

    public UserServiceImpl(UserRepository userRepository) {
        this.userRepository = userRepository;
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

        return userRepository.save(user);
    }

    @Override
    public Optional<User> findById(Long id) {
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
    public User update(Long id, User updatedUser) throws NotFoundException, ConflictException {
        Optional<User> userOptional = userRepository.findById(id);

        if (userOptional.isEmpty()) {
            String message = "User with the id " + id + " does not exist";
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

        updatedUser.setId(id);
        updatedUser.setPassword(user.getPassword());
        updatedUser.setRole(user.getRole());

        return userRepository.save(updatedUser);
    }

    @Override
    public void updatePassword(Long id, String password) throws NotFoundException {
        Optional<User> userOptional = userRepository.findById(id);

        if (userOptional.isEmpty()) {
            String message = "User with the id " + id + " does not exist";
            log.error(message);
            throw new NotFoundException(message);
        }

        User user = userOptional.get();
        user.setPassword(password);

        userRepository.save(user);
    }

    @Override
    public void updateRole(Long id, UserRole role) throws NotFoundException {
        Optional<User> userOptional = userRepository.findById(id);

        if (userOptional.isEmpty()) {
            String message = "User with the id " + id + " does not exist";
            log.error(message);
            throw new NotFoundException(message);
        }

        User user = userOptional.get();
        user.setRole(role);

        userRepository.save(user);
    }

    @Override
    public void delete(Long id) throws NotFoundException {
        if (!userRepository.existsById(id)) {
            String message = "User with the id " + id + " does not exist";
            log.error(message);
            throw new NotFoundException(message);
        }

        userRepository.deleteById(id);
    }
}
