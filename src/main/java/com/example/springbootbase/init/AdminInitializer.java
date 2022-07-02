package com.example.springbootbase.init;

import com.example.springbootbase.domain.AppUser;
import com.example.springbootbase.domain.enumeration.AppUserRole;
import com.example.springbootbase.service.AppUserService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.stereotype.Component;

@Component
@Slf4j
public class AdminInitializer implements ApplicationRunner {
    private final AppUserService appUserService;

    public AdminInitializer(AppUserService appUserService) {
        this.appUserService = appUserService;
    }

    @Value("${security.admin.username}")
    private String adminUsername;

    @Value("${security.admin.email}")
    private String adminEmail;

    @Value("${security.admin.password}")
    private String adminPassword;

    @Override
    public void run(ApplicationArguments args) throws Exception {
        if (appUserService.findByUsername(adminUsername).isEmpty()) {
            log.info(
                    "Admin user with the username {} does not exist. Creating admin user.",
                    adminUsername
            );

            AppUser appUser = AppUser.builder()
                    .username(adminUsername)
                    .email(adminEmail)
                    .password(adminPassword)
                    .build();

            AppUser savedAppUser = appUserService.save(appUser);
            appUserService.updateRoleByUsername(savedAppUser.getUsername(), AppUserRole.ROLE_ADMIN);
        }
    }
}
