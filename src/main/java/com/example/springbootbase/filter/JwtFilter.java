package com.example.springbootbase.filter;

import com.auth0.jwt.exceptions.JWTVerificationException;
import com.auth0.jwt.exceptions.SignatureVerificationException;
import com.auth0.jwt.exceptions.TokenExpiredException;
import com.example.springbootbase.utility.JwtUtility;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpHeaders;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.web.util.matcher.AntPathRequestMatcher;
import org.springframework.security.web.util.matcher.RequestMatcher;
import org.springframework.web.filter.OncePerRequestFilter;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

@Configuration
@Slf4j
public class JwtFilter extends OncePerRequestFilter {
    private final UserDetailsService userDetailsService;
    private final JwtUtility jwtUtility;
    private final String tokenPrefix = "Bearer ";

    private final List<RequestMatcher> ignoredPaths = new ArrayList<>();

    public JwtFilter(
            UserDetailsService userDetailsService,
            JwtUtility jwtUtility) {

        this.userDetailsService = userDetailsService;
        this.jwtUtility = jwtUtility;

        this.ignoredPaths.add(new AntPathRequestMatcher("/api/auth/login/**"));
        this.ignoredPaths.add(new AntPathRequestMatcher("/api/auth/register/**"));
        this.ignoredPaths.add(new AntPathRequestMatcher("/api/auth/refresh-access-token/**"));
    }

    @Override
    protected void doFilterInternal(
            HttpServletRequest request,
            HttpServletResponse response,
            FilterChain filterChain) throws ServletException, IOException {

        /* Some paths do not get filtered by this filter. The permitAll in security
         * configuration only checks if authenticated user is present in the security
         * context, it does not disable the filter chain as it is needed for authentication.
         * Because of that, without path ignoring this filter would still return the
         * error response on paths where permitAll is present.  */
        for (RequestMatcher ignoredPath : ignoredPaths) {
            if (ignoredPath.matches(request)) {
                filterChain.doFilter(request, response);
                return;
            }
        }

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
