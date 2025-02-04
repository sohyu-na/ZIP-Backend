package com.capstone.bszip.Member.security;

import io.jsonwebtoken.*;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.security.Keys;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.security.Key;
import java.util.Date;
@Component
public class JwtUtil {
    private static String SECRET_KEY;
    private static Key key;

    @Value("${jwt.secret}")
    public void setSecretKey(String secret) {
        SECRET_KEY = secret;
        byte[] keyBytes = Decoders.BASE64.decode(SECRET_KEY);
        key = Keys.hmacShaKeyFor(keyBytes);
    }

    private static final long TEMP_TOKEN_EXPIRATION = 1000 * 60 * 30; // 30분
    private static final long EXPIRATION_TIME = 1000 * 60 * 15; // 15분

    private static Key getSigningKey() {
        if(key==null){
            throw new IllegalStateException("jwt key가 초기화되지 않음");
        }
        return key;
    }

    // 이메일 임시 토큰 생성
    public static String issueTempToken(String email) {
        Date now = new Date();

        return Jwts.builder()
                .setSubject(email)
                .setIssuedAt(now)
                .setExpiration(new Date(now.getTime() + TEMP_TOKEN_EXPIRATION))
                .signWith(getSigningKey(), SignatureAlgorithm.HS256)
                .compact();
    }
    // 토큰 생성
    public static String generateToken(String email) {
        return Jwts.builder()
                .setSubject(email)
                .setIssuedAt(new Date())
                .setExpiration(new Date(System.currentTimeMillis() + EXPIRATION_TIME))
                .signWith(getSigningKey(), SignatureAlgorithm.HS256)
                .compact();
    }

    // 토큰에서 이메일 추출
    public static String extractEmail(String token) {
        try {
            return Jwts.parserBuilder()
                    .setSigningKey(getSigningKey())
                    .build()
                    .parseClaimsJws(token.replace("Bearer ", ""))
                    .getBody()
                    .getSubject();
        } catch (Exception e) {
            throw new JwtException("유효하지 않은 토큰입니다.");
        }
    }

    // JWT 토큰에서 클레임 추출하는 함수
    public static Claims extractToken(String token) {
        return Jwts.parserBuilder()
                .setSigningKey(getSigningKey())
                .build()
                .parseClaimsJws(token)
                .getBody();
    }
}

