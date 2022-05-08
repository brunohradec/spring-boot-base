package com.example.springbootbase.mapper;

import com.example.springbootbase.domain.User;
import com.example.springbootbase.dto.UserDto;
import com.example.springbootbase.dto.command.UserRegistrationCommand;
import com.example.springbootbase.dto.command.UserUpdateCommand;
import org.springframework.stereotype.Component;

@Component
public class UserMapper {
    public UserDto toDto(User user) {
        return UserDto.builder()
                .id(user.getId())
                .firstName(user.getFirstName())
                .lastName(user.getLastName())
                .username(user.getUsername())
                .email(user.getEmail())
                .role(user.getRole())
                .build();
    }

    public User toEntity(UserRegistrationCommand userRegistrationCommand) {
        return User.builder()
                .firstName(userRegistrationCommand.getFirstName())
                .lastName(userRegistrationCommand.getLastName())
                .username(userRegistrationCommand.getUsername())
                .email(userRegistrationCommand.getEmail())
                .password(userRegistrationCommand.getPassword())
                .build();
    }

    public User toEntity(UserUpdateCommand userUpdateCommand) {
        return User.builder()
                .firstName(userUpdateCommand.getFirstName())
                .lastName(userUpdateCommand.getLastName())
                .username(userUpdateCommand.getUsername())
                .email(userUpdateCommand.getEmail())
                .build();
    }
}
