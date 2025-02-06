package com.capstone.bszip.Member.service.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Builder
@Schema(description = "이메일 메시지 DTO")
public class EmailMessage {
    @Schema(description = "수신 이메일",example = "bszip@naver.com")
    private String to;
    @Schema(description = "메일 제목", example = "[서점ZIP] 임시 비밀번호 발급")
    private String subject;
    @Schema(description = "메일 내용", example = "임시 비밀번호")
    private String message;
}
