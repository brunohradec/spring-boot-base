package com.example.springbootbase.dto.command;

import com.example.springbootbase.domain.enumeration.UserRole;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class UserRoleUpdateCommand {
    private UserRole role;
}
