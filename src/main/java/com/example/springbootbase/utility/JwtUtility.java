package com.example.springbootbase.utility;

import com.auth0.jwt.JWT;
import com.auth0.jwt.JWTVerifier;
import com.auth0.jwt.algorithms.Algorithm;
import com.auth0.jwt.exceptions.JWTVerificationException;
import com.auth0.jwt.exceptions.SignatureVerificationException;
import com.auth0.jwt.exceptions.TokenExpiredException;
import com.auth0.jwt.interfaces.DecodedJWT;
import com.example.springbootbase.domain.AppUser;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.util.Date;

@Component
@Slf4j
public class JwtUtility {
    @Value("${security.jwt.issuer}")
    private String issuer;

    @Value("${security.jwt.access-token.secret}")
    private String accessTokenSecret;

    @Value("${security.jwt.access-token.expiration-time-millis}")
    private Long accessTokenExpirationTimeMillis;

    @Value("${security.jwt.refresh-token.secret}")
    private String refreshTokenSecret;

    @Value("${security.jwt.refresh-token.expiration-time-millis}")
    private Long refreshTokenExpirationTimeMillis;

    public String generateAccessToken(AppUser appUser) {
        return JWT.create()
                .withIssuer(issuer)
                .withIssuedAt(new Date())
                .withSubject(appUser.getUsername())
                .withExpiresAt(new Date(System.currentTimeMillis() + accessTokenExpirationTimeMillis))
                .withClaim("email", appUser.getEmail())
                .withClaim("role", appUser.getRole().name())
                .sign(Algorithm.HMAC256(accessTokenSecret));
    }

    public String generateRefreshToken(AppUser appUser) {
        return JWT.create()
                .withIssuer(issuer)
                .withIssuedAt(new Date())
                .withSubject(appUser.getUsername())
                .withExpiresAt(new Date(System.currentTimeMillis() + refreshTokenExpirationTimeMillis))
                .sign(Algorithm.HMAC256(refreshTokenSecret));
    }

    public String validateAccessTokenAndRetrieveSubject(String accessToken) throws
            SignatureVerificationException,
            TokenExpiredException {

        JWTVerifier jwtVerifier = JWT.require(Algorithm.HMAC256(accessTokenSecret)).withIssuer(issuer).build();

        try {
            DecodedJWT decodedJWT = jwtVerifier.verify(accessToken);
            return decodedJWT.getSubject();
        } catch (SignatureVerificationException exception) {
            log.debug("Access token signature not valid");
            throw exception;
        } catch (TokenExpiredException exception) {
            log.debug("Access token has expired");
            throw exception;
        } catch (JWTVerificationException exception) {
            log.debug("Access token is not valid.", exception);
            throw exception;
        }
    }

    public String validateRefreshTokenAndRetrieveSubject(String refreshToken) throws
            SignatureVerificationException,
            TokenExpiredException {

        JWTVerifier jwtVerifier = JWT.require(Algorithm.HMAC256(refreshTokenSecret)).withIssuer(issuer).build();

        try {
            DecodedJWT decodedJWT = jwtVerifier.verify(refreshToken);
            return decodedJWT.getSubject();
        } catch (SignatureVerificationException exception) {
            log.debug("Access token signature not valid");
            throw exception;
        } catch (TokenExpiredException exception) {
            log.debug("Access token has expired");
            throw exception;
        } catch (JWTVerificationException exception) {
            log.debug("Access token is not valid.", exception);
            throw exception;
        }
    }
}
