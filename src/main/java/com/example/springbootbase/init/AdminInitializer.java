package com.example.springbootbase.init;

import com.example.springbootbase.domain.User;
import com.example.springbootbase.domain.enumeration.UserRole;
import com.example.springbootbase.service.UserService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.stereotype.Component;

@Component
@Slf4j
public class AdminInitializer implements ApplicationRunner {
    private final UserService userService;

    public AdminInitializer(UserService userService) {
        this.userService = userService;
    }

    @Value("${security.admin.username}")
    private String adminUsername;

    @Value("${security.admin.email}")
    private String adminEmail;

    @Value("${security.admin.password}")
    private String adminPassword;

    @Override
    public void run(ApplicationArguments args) throws Exception {
        if (userService.findByUsername(adminUsername).isEmpty()) {
            log.info(
                    "Admin user with the username {} does not exist. Creating admin user.",
                    adminUsername
            );

            User user = User.builder()
                    .username(adminUsername)
                    .email(adminEmail)
                    .password(adminPassword)
                    .build();

            User savedUser = userService.save(user);
            userService.updateRole(savedUser.getId(), UserRole.ADMIN);
        }
    }
}
