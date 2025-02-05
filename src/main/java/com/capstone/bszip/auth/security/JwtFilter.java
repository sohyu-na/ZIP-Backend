package com.capstone.bszip.auth.security;

import io.jsonwebtoken.Claims;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;

@Component
public class JwtFilter extends OncePerRequestFilter {
    @Override
    protected void doFilterInternal(HttpServletRequest request,
                                    HttpServletResponse response,
                                    FilterChain filterChain
    ) throws ServletException, IOException {
        //헤더에서 Authorization
        String authHeader = request.getHeader("Authorization");
        String jwtToken = null;
        if (authHeader != null && authHeader.startsWith("Bearer ")) {
            jwtToken = authHeader.substring(7); //token 추출
        } else {
            filterChain.doFilter(request, response);
            return;
        }

        Claims claims;
        try{
            claims = JwtUtil.extractToken(jwtToken);
        } catch (Exception e) {
            filterChain.doFilter(request, response); //종료되지 않고 다음 필터
            return;
        }

        System.out.println("Claims: " + claims);

        filterChain.doFilter(request, response);
    }
}
