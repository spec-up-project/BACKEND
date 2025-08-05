package com.neekly_report.whirlwind.common.Jwt;

import io.jsonwebtoken.*;
import io.jsonwebtoken.security.Keys;
import org.springframework.stereotype.Component;

import java.security.Key;
import java.util.Date;

@Component
public class JwtUtil {
    private final String secret = "your-256-bit-secret-your-256-bit-secret"; // 32자 이상
    private final long expirationMs = 1000 * 60 * 60; // 1시간

    private Key getKey() {
        return Keys.hmacShaKeyFor(secret.getBytes());
    }

    public String generateToken(String tUserUid, String email) {
        return Jwts.builder()
                .setSubject(tUserUid)
                .claim("email", email)
                .setIssuedAt(new Date())
                .setExpiration(new Date(System.currentTimeMillis() + expirationMs))
                .signWith(getKey(), SignatureAlgorithm.HS256)
                .compact();
    }

    public Claims validateAndGetClaims(String token) {
        return Jwts.parserBuilder()
                .setSigningKey(getKey())
                .build()
                .parseClaimsJws(token)
                .getBody();
    }

    public String generateRefreshToken(String tUserUid, String email) {
        long refreshTokenExpirationMs = 1000L * 60 * 60 * 24 * 7; // 7일
        return Jwts.builder()
                .setSubject(tUserUid)
                .claim("email", email)
                .setIssuedAt(new Date())
                .setExpiration(new Date(System.currentTimeMillis() + refreshTokenExpirationMs))
                .signWith(getKey(), SignatureAlgorithm.HS256)
                .compact();
    }
}