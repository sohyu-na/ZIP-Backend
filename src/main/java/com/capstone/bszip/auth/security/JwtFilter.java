package com.capstone.bszip.auth.security;

import com.capstone.bszip.Member.domain.Member;
import com.capstone.bszip.Member.repository.MemberRepository;
import io.jsonwebtoken.Claims;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.util.List;

@Component
public class JwtFilter extends OncePerRequestFilter {
    private final JwtUtil jwtUtil;
    private final MemberRepository memberRepository;

    public JwtFilter(JwtUtil jwtUtil, MemberRepository memberRepository) {
        this.jwtUtil = jwtUtil;
        this.memberRepository = memberRepository;
    }

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
            String email = JwtUtil.extractEmail(jwtToken);
            Member member = memberRepository.findByEmail(email)
                    .orElseThrow(()-> new UsernameNotFoundException("Username not found: "+ email));
            // 그냥 다 USER로 했음.. 추후 수정해야 될듯...
            List<GrantedAuthority> authorities = List.of(new SimpleGrantedAuthority("ROLE_USER"));
            UsernamePasswordAuthenticationToken authentication = new UsernamePasswordAuthenticationToken(member, null, authorities);

            SecurityContextHolder.getContext().setAuthentication(authentication);

        } catch (Exception e) {
            filterChain.doFilter(request, response); //종료되지 않고 다음 필터
            return;
        }

        System.out.println("Claims: " + claims);

        filterChain.doFilter(request, response);
    }
}
