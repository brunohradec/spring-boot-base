package com.example.springbootbase.dto.command;

import com.example.springbootbase.validation.constraints.ValuesEqual;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.validation.constraints.Email;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@ValuesEqual(
        fields = {"password", "repeatedPassword"}
)
public class AppUserRegistrationCommand {
    private String firstName;
    private String lastName;

    @NotNull
    @NotBlank
    private String username;

    @NotNull
    @NotBlank
    @Email
    private String email;

    @NotNull
    @NotBlank
    @Size(min = 8)
    private String password;

    @NotNull
    private String repeatedPassword;
}
