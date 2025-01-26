package com.capstone.bszip.Member.service.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Schema(description = "회원가입 요청 객체")
//@Builder
public class SignupRequest {
    @Schema(description = "사용자 이메일", example = "user@example.com", required = true)
    private String email;
    @Schema(description = "사용자 비밀번호", example = "password123", required = true)
    private String password;
}
