package com.example.springbootbase.dto;

import com.example.springbootbase.domain.enumeration.AppUserRole;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class AppUserDto {
    private Long id;
    private String firstName;
    private String lastName;
    private String username;
    private String email;
    private AppUserRole role;
}
