package com.example.springbootbase.filter;

import com.auth0.jwt.exceptions.JWTVerificationException;
import com.auth0.jwt.exceptions.SignatureVerificationException;
import com.auth0.jwt.exceptions.TokenExpiredException;
import com.example.springbootbase.utility.JwtUtility;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpHeaders;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.web.filter.OncePerRequestFilter;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

/* JwtFilter is not a component or a bean. It has to be instantiated
 * manually when adding it to the filter chain in security config.
 * Other beans needed by the JwtFilter must be injected manually during
 * object creation by providing the references to them to the constructor.
 * JwtFilter must not be a component or a bean as spring security would
 * then pick it up automatically and add it to the filter chain behind
 * default built-in spring security filters. That would cause it to be added
 * twice, once in the security config in addFilterBefore method and once by
 * spring security. */

@Slf4j
public class JwtFilter extends OncePerRequestFilter {
    private final UserDetailsService userDetailsService;
    private final JwtUtility jwtUtility;
    private final String tokenPrefix = "Bearer ";

    public JwtFilter(
            UserDetailsService userDetailsService,
            JwtUtility jwtUtility) {

        this.userDetailsService = userDetailsService;
        this.jwtUtility = jwtUtility;
    }

    @Override
    protected void doFilterInternal(
            HttpServletRequest request,
            HttpServletResponse response,
            FilterChain filterChain) throws ServletException, IOException {

        String authorizationHeader = request.getHeader(HttpHeaders.AUTHORIZATION);

        // Access token is provided in the authorization header.
        if (authorizationHeader == null || authorizationHeader.isBlank()) {
            String message = "Request authorization header not found or empty.";
            log.debug(message);
            response.sendError(HttpServletResponse.SC_UNAUTHORIZED, message);

            return;
        }

        // Access token is provided in the authorization header in the format "Bearer ACCESS_TOKEN".
        if (authorizationHeader.isBlank() || !authorizationHeader.startsWith(tokenPrefix)) {
            String message = "Request authorization header does not contain the bearer token.";
            log.debug(message);
            response.sendError(HttpServletResponse.SC_UNAUTHORIZED, message);

            return;
        }

        String accessToken = authorizationHeader.substring(tokenPrefix.length());

        if (accessToken.isBlank()) {
            String message = "Access token not found in the request authorization header.";
            log.debug(message);
            response.sendError(HttpServletResponse.SC_UNAUTHORIZED, message);

            return;
        }

        try {
            String username = jwtUtility.validateAccessTokenAndRetrieveSubject(accessToken);
            UserDetails userDetails = userDetailsService.loadUserByUsername(username);

            UsernamePasswordAuthenticationToken authenticationToken = new UsernamePasswordAuthenticationToken(
                    userDetails.getUsername(),
                    userDetails.getPassword(),
                    userDetails.getAuthorities()
            );

            log.debug(
                    "JWT check successful. User with username {} saved to security context.",
                    userDetails.getUsername()
            );

            SecurityContextHolder.getContext().setAuthentication(authenticationToken);
        } catch (SignatureVerificationException exception) {
            String message = "Could not verify access token signature.";
            log.debug(message);
            response.sendError(HttpServletResponse.SC_UNAUTHORIZED, message);

            return;
        } catch (TokenExpiredException exception) {
            String message = "Access token has timed out.";
            log.debug(message);
            response.sendError(HttpServletResponse.SC_UNAUTHORIZED, message);

            return;
        } catch (JWTVerificationException exception) {
            String message = "Access token is not valid.";
            log.debug(message);
            response.sendError(HttpServletResponse.SC_UNAUTHORIZED, message);

            return;
        }

        filterChain.doFilter(request, response);
    }
}
