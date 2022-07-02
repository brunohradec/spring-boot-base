package com.example.springbootbase.dto.command;

import com.example.springbootbase.domain.enumeration.AppUserRole;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.validation.constraints.NotNull;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class UserUpdateRoleCommand {
    @NotNull
    private AppUserRole role;
}
