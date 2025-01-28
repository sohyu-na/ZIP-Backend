package com.capstone.bszip.Member.controller;

import com.capstone.bszip.Member.security.JwtUtil;
import com.capstone.bszip.Member.service.MemberService;
import com.capstone.bszip.Member.service.dto.JwtResponse;
import com.capstone.bszip.Member.service.dto.LoginRequest;
import com.capstone.bszip.Member.service.dto.SignupAddRequest;
import com.capstone.bszip.Member.service.dto.SignupRequest;
import io.jsonwebtoken.JwtException;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@Tag(name = "Member", description = "회원 관리 API")
public class MemberController {
    private final MemberService memberService;

    @Autowired
    public MemberController(MemberService memberService) {
        this.memberService = memberService;
    }

    //회원 가입
    @PostMapping("/signup")
    @Operation(summary = "회원 가입", description = "이메일과 비밀번호로 회원 가입을 진행합니다.")
    @ApiResponse(responseCode = "200", description = "회원가입-이메일,비밀번호 성공",
            content = @Content(schema = @Schema(implementation = Map.class)))
    @ApiResponse(responseCode = "500", description = "서버 오류",
            content = @Content(schema = @Schema(implementation = Map.class)))
    public ResponseEntity<?> signup(@RequestBody SignupRequest signupRequest){
        //회원 정보 저장
        try {
            // 이메일 및 비밀번호 저장
            memberService.registerMember(signupRequest);
            // 이메일을 포함한 JWT 토큰 발급
            String token = JwtUtil.generateToken(signupRequest.getEmail());
            return ResponseEntity.ok(Map.of("message", "회원가입-이메일,비밀번호 성공", "token", token));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(Map.of("message", e.getMessage()));
        }
    }
    //회원 가입-닉네임
    @PostMapping("/signup/add")
    @SecurityRequirement(name = "jwtAuth")
    @Operation(summary = "회원 가입 추가 정보", description = "닉네임을 추가하여 회원 가입을 완료합니다.")
    @ApiResponse(responseCode = "200", description = "회원가입 완료",
            content = @Content(schema = @Schema(implementation = Map.class)))
    @ApiResponse(responseCode = "401", description = "유효하지 않은 토큰",
            content = @Content(schema = @Schema(implementation = Map.class)))
    @ApiResponse(responseCode = "500", description = "서버 오류",
            content = @Content(schema = @Schema(implementation = Map.class)))
    //이메일,비밀번호->닉네임으로 넘어갈때 헤더에 token 포함 ..
    public ResponseEntity<?> add(
            @Parameter(description = "이메일 생성 토큰", required = true) @RequestHeader("Authorization") String token,
            @RequestBody SignupAddRequest signupAddRequest){
        try {
            token = token.substring(7);
            // 토큰에서 이메일 추출
            String email = JwtUtil.extractEmail(token);

            // 닉네임과 이메일을 이용해 최종 회원가입
            memberService.registerMemberNickname(email, signupAddRequest.getNickname());

            return ResponseEntity.ok(Map.of("message", "회원가입 완료"));
        } catch (JwtException e) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                    .body(Map.of("message", "유효하지 않은 토큰입니다."));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(Map.of("message", e.getMessage()));
        }
    }
    //로그인
    @PostMapping("/login")
    @Operation(summary = "로그인", description = "이메일과 비밀번호로 로그인합니다.")
    @ApiResponse(responseCode = "200", description = "로그인 성공",
            content = @Content(schema = @Schema(implementation = JwtResponse.class)))
    @ApiResponse(responseCode = "500", description = "서버 오류",
            content = @Content(schema = @Schema(implementation = Map.class)))
    public ResponseEntity<?> login(@RequestBody LoginRequest loginRequest){
        try{
            String token = memberService.loginUser(loginRequest);
            return ResponseEntity.ok(new JwtResponse(token, "Bearer"));
        } catch(Exception e){
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(Map.of("message", e.getMessage()));
        }
    }
}

