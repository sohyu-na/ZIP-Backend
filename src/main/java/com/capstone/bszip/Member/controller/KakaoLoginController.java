package com.capstone.bszip.Member.controller;

import com.capstone.bszip.Member.service.KakaoService;
import com.capstone.bszip.Member.service.MemberService;
import com.capstone.bszip.Member.service.dto.SignupRequest;
import com.capstone.bszip.auth.AuthService;
import com.capstone.bszip.auth.dto.TokenResponse;
import com.capstone.bszip.auth.security.JwtUtil;
import com.capstone.bszip.commonDto.ErrorResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/kakao/oauth")
@Tag(name="카카오 로그인", description = "카카오 로그인")
public class KakaoLoginController {
    private final KakaoService kakaoService;
    private final MemberService memberService;
    private final AuthService authService;

    @ResponseBody
    @GetMapping("/login")
    public ResponseEntity<?> kakaoLogin(@RequestParam String code, HttpServletResponse response){

        String kakaoEmail = kakaoService.getkakaoEmail(code);
        int loginWay = kakaoService.whichLoginWay(kakaoEmail);



       if(loginWay == 2){
           return ResponseEntity.status(409).body(
                   ErrorResponse.builder()
                   .result(false)
                   .status(409)
                           .message("기본 로그인으로 로그인해야 합니다.")
                           .build()
           );
       }

       if(loginWay == 3){
           try{
               TokenResponse tokens = kakaoService.loginUser(kakaoEmail);
               authService.login(kakaoEmail,tokens.getRefreshToken());

               return ResponseEntity.ok(tokens);
           }catch (BadCredentialsException e) {
               return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                       .body(Map.of("error", "잘못된 접근"));
           }catch(Exception e){
               return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                       .body(Map.of("message", e.getMessage()));
           }

       }

       if(loginWay == 1) {
           try{
           SignupRequest signupRequest = new SignupRequest();
           signupRequest.setEmail(kakaoEmail);
           signupRequest.setPassword("kakao-password");
           memberService.registerMember(signupRequest);

           String token = JwtUtil.issueTempToken(kakaoEmail);
           return ResponseEntity.ok()
                   .header(HttpHeaders.AUTHORIZATION, "Bearer " + token)
                   .body(Map.of("message", "TEMP_TOKEN_ISSUED"));
            }
           catch (Exception e) {
               return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                       .body(Map.of("message", e.getMessage()));
           }
       }

        return ResponseEntity.status(400).body(
                ErrorResponse.builder()
                .result(false)
                .status(400)
                        .message("Internal Server Error")
                        .build()
        );

    }

}
