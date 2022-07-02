package com.example.springbootbase.mapper;

import com.example.springbootbase.domain.AppUser;
import com.example.springbootbase.dto.AppUserDto;
import com.example.springbootbase.dto.command.AppUserRegistrationCommand;
import com.example.springbootbase.dto.command.AppUserUpdateCommand;
import org.springframework.stereotype.Component;

@Component
public class AppUserMapper {
    public AppUserDto toDto(AppUser appUser) {
        return AppUserDto.builder()
                .id(appUser.getId())
                .firstName(appUser.getFirstName())
                .lastName(appUser.getLastName())
                .username(appUser.getUsername())
                .email(appUser.getEmail())
                .role(appUser.getRole())
                .build();
    }

    public AppUser toEntity(AppUserRegistrationCommand appUserRegistrationCommand) {
        return AppUser.builder()
                .firstName(appUserRegistrationCommand.getFirstName())
                .lastName(appUserRegistrationCommand.getLastName())
                .username(appUserRegistrationCommand.getUsername())
                .email(appUserRegistrationCommand.getEmail())
                .password(appUserRegistrationCommand.getPassword())
                .build();
    }

    public AppUser toEntity(AppUserUpdateCommand appUserUpdateCommand) {
        return AppUser.builder()
                .firstName(appUserUpdateCommand.getFirstName())
                .lastName(appUserUpdateCommand.getLastName())
                .username(appUserUpdateCommand.getUsername())
                .email(appUserUpdateCommand.getEmail())
                .build();
    }
}
