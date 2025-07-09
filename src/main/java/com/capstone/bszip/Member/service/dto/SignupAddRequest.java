package com.capstone.bszip.Member.service.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Schema(description = "회원가입 추가 정보 요청 객체")
public class SignupAddRequest {
    @Schema(description = "사용자 닉네임", example = "이구역독서짱", required = true)
    private String nickname;
}
