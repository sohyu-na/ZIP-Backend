package com.capstone.bszip.Member.service.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Getter;

@Getter
@Schema(description = "이메일 요청 DTO")
public class EmailRequest {
    @Schema(description = "수신 이메일",example = "bszip@naver.com")
    String email;
}
