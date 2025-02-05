package com.capstone.bszip.commonDto;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
@Schema(description = "API 에러 응답 DTO")
public class ErrorResponse {

    @Schema(description = "API 요청 성공 여부", example = "false")
    private final boolean result;

    @Schema(description = "HTTP 상태 코드", example = "400")
    private final int status;

    @Schema(description = "에러 메시지", example = "잘못된 요청입니다.")
    private final String message;

    @Schema(description = "에러 상세 내용", nullable = true)
    private final String detail;
}
