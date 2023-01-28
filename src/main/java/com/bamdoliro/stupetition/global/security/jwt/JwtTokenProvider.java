package com.bamdoliro.stupetition.global.security.jwt;

import com.bamdoliro.stupetition.domain.user.domain.User;
import com.bamdoliro.stupetition.global.security.jwt.exception.ExpiredTokenException;
import com.bamdoliro.stupetition.global.security.jwt.exception.InvalidTokenException;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.ExpiredJwtException;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.security.Keys;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import javax.servlet.http.HttpServletRequest;
import java.nio.charset.StandardCharsets;
import java.security.Key;
import java.util.Date;

import static com.bamdoliro.stupetition.global.security.jwt.JwtProperties.JWT_HEADER;
import static com.bamdoliro.stupetition.global.security.jwt.JwtProperties.JWT_PREFIX;

@Component
public class JwtTokenProvider {

    @Value("${spring.jwt.secret}")
    private String SECRET_KEY;

    private Key getSigningKey(String secretKey) {
        byte[] keyBytes = secretKey.getBytes(StandardCharsets.UTF_8);
        return Keys.hmacShaKeyFor(keyBytes);
    }

    public String createAccessToken(User user) {
        return createToken(user, JwtProperties.ACCESS_TOKEN_VALID_TIME);
    }

    public String createRefreshToken(User user) {
        return createToken(user, JwtProperties.REFRESH_TOKEN_VALID_TIME);
    }

    public String createToken(User user, long time) {
        Claims claims = Jwts.claims();
        claims.put("email", user.getEmail());
        claims.put("userId", user.getId());
        Date now = new Date();

        return Jwts.builder()
                .setClaims(claims)
                .setIssuedAt(now)
                .setExpiration(new Date(now.getTime() + time))
                .signWith(getSigningKey(SECRET_KEY), SignatureAlgorithm.HS256)
                .compact();
    }

    public Claims extractAllClaims(String token) {
        try {
            return Jwts.parserBuilder()
                    .setSigningKey(getSigningKey(SECRET_KEY))
                    .build()
                    .parseClaimsJws(token)
                    .getBody();
        } catch (ExpiredJwtException e) {
            throw ExpiredTokenException.EXCEPTION;
        } catch (Exception e) {
            e.printStackTrace();
            throw InvalidTokenException.EXCEPTION;
        }
    }

    public String resolveToken(HttpServletRequest request) {
        String bearer = request.getHeader(JWT_HEADER);
        return parseToken(bearer);
    }

    public String parseToken(String bearerToken) {
        if (bearerToken != null && bearerToken.startsWith(JWT_PREFIX))
            return bearerToken.replace(JWT_PREFIX, "");
        return null;
    }
}