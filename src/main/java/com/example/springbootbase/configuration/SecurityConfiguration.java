package com.example.springbootbase.configuration;

import com.example.springbootbase.filter.JwtFilter;
import com.example.springbootbase.utility.JwtUtility;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.method.configuration.EnableGlobalMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.builders.WebSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

@Configuration
@EnableWebSecurity
@EnableGlobalMethodSecurity(
        prePostEnabled = true,
        securedEnabled = true,
        jsr250Enabled = true
)
public class SecurityConfiguration extends WebSecurityConfigurerAdapter {
    private final UserDetailsService userDetailsService;
    private final JwtUtility jwtUtility;

    public SecurityConfiguration(
            UserDetailsService userDetailsService,
            JwtUtility jwtUtility) {

        this.userDetailsService = userDetailsService;
        this.jwtUtility = jwtUtility;
    }

    @Override
    public void configure(WebSecurity web) throws Exception {
        web.ignoring()
                .antMatchers("/api/auth/register")
                .antMatchers("/api/auth/login")
                .antMatchers("/api/auth/refresh-access-token");
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
                .antMatchers("/api/auth/**")
                .permitAll()
                .antMatchers("/api/**")
                .authenticated();

        /* JwtFilter has to be instantiated manually here and userDetailsService
         * and jwtUtility have to be injected manually as JwtFilter must not
         * be a component or a bean as spring security would then pick it up
         * and add it to the filter chain automatically behind default built-in
         * spring security filters. That would cause it to be added twice,
         * once here and once by spring security. */
        http.addFilterBefore(
                new JwtFilter(userDetailsService, jwtUtility),
                UsernamePasswordAuthenticationFilter.class
        );
    }

    @Bean
    @Override
    public AuthenticationManager authenticationManagerBean() throws Exception {
        return super.authenticationManagerBean();
    }
}
