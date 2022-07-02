package com.example.springbootbase.configuration;

import com.example.springbootbase.domain.enumeration.AppUserRole;
import com.example.springbootbase.filter.JwtFilter;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

@Configuration
@EnableWebSecurity
public class SecurityConfiguration extends WebSecurityConfigurerAdapter {
    private final UserDetailsService userDetailsService;
    private final JwtFilter jwtFilter;

    public SecurityConfiguration(
            UserDetailsService userDetailsService,
            JwtFilter jwtFilter) {

        this.userDetailsService = userDetailsService;
        this.jwtFilter = jwtFilter;
    }

    @Override
    protected void configure(HttpSecurity http) throws Exception {
        http.csrf().disable();
        http.httpBasic().disable();
        http.sessionManagement().sessionCreationPolicy(SessionCreationPolicy.STATELESS);
        http.cors();
        http.userDetailsService(userDetailsService);

        http.authorizeHttpRequests()
                .antMatchers("/api/auth/me/**")
                    .authenticated()
                .antMatchers(HttpMethod.PUT, "/api/users/{username}/role/**")
                    .hasRole(AppUserRole.ADMIN.name())
                .antMatchers(HttpMethod.PUT, "/api/users/{username}/password/**")
                    .hasRole(AppUserRole.ADMIN.name())
                .antMatchers(HttpMethod.DELETE, "/api/users/{username}/**")
                    .hasRole(AppUserRole.ADMIN.name())
                .antMatchers("/api/auth/**")
                    .permitAll()
                .antMatchers("/api/**")
                    .authenticated();

        http.addFilterBefore(jwtFilter, UsernamePasswordAuthenticationFilter.class);
    }

    @Bean
    @Override
    public AuthenticationManager authenticationManagerBean() throws Exception {
        return super.authenticationManagerBean();
    }
}
