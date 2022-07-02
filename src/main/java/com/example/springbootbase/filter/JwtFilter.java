package com.example.springbootbase.filter;

import com.auth0.jwt.exceptions.JWTVerificationException;
import com.auth0.jwt.exceptions.SignatureVerificationException;
import com.auth0.jwt.exceptions.TokenExpiredException;
import com.example.springbootbase.utility.JwtUtility;
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
            response.sendError(
                    HttpServletResponse.SC_UNAUTHORIZED,
                    "Authorization header not found or empty."
            );

            return;
        }

        // Access token is provided in the authorization header in the format "Bearer ACCESS_TOKEN".
        if (authorizationHeader.isBlank() || !authorizationHeader.startsWith(tokenPrefix)) {
            response.sendError(
                    HttpServletResponse.SC_UNAUTHORIZED,
                    "Authorization header does not contain the bearer token."
            );

            return;
        }

        String accessToken = authorizationHeader.substring(tokenPrefix.length());

        if (accessToken.isBlank()) {
            response.sendError(
                    HttpServletResponse.SC_UNAUTHORIZED,
                    "Access token not found in the authorization header."
            );

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

            SecurityContextHolder.getContext().setAuthentication(authenticationToken);
        } catch (SignatureVerificationException exception) {
            response.sendError(
                    HttpServletResponse.SC_UNAUTHORIZED,
                    "Could not verify access token signature."
            );

            return;
        } catch (TokenExpiredException exception) {
            response.sendError(
                    HttpServletResponse.SC_UNAUTHORIZED,
                    "Access token has timed out."
            );

            return;
        } catch (JWTVerificationException exception) {
            response.sendError(
                    HttpServletResponse.SC_UNAUTHORIZED,
                    "Access token is not valid."
            );

            return;
        }

        filterChain.doFilter(request, response);
    }
}
