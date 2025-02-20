package com.capstone.bszip.Member.controller;

import com.capstone.bszip.Member.service.MemberService;
import com.capstone.bszip.Member.service.PasswordManagementService;
import com.capstone.bszip.Member.service.dto.EmailMessage;
import com.capstone.bszip.Member.service.dto.EmailRequest;
import com.capstone.bszip.commonDto.ErrorResponse;
import com.capstone.bszip.commonDto.SuccessResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.AuthenticationException;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.client.HttpClientErrorException;


@RestController
@RequestMapping("/api")
@Tag(name="TempPassword", description = "임시 비밀번호")
public class PasswordManagementController {
    private final PasswordManagementService passwordManagementService;
    private final MemberService memberService;

    public PasswordManagementController(PasswordManagementService passwordManagementService, MemberService memberService) {
        this.passwordManagementService = passwordManagementService;
        this.memberService = memberService;
    }
    @Operation(summary = "임시 비밀번호 재설정 및 메일 전송")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "임시 비밀번호 발급 및 메일 전송 성공",
                    content = @Content(schema = @Schema(implementation = SuccessResponse.class))),
            @ApiResponse(responseCode = "401", description = "등록된 이메일을 찾을 수 없음",
                    content = @Content(schema = @Schema(implementation = ErrorResponse.class))),
            @ApiResponse(responseCode = "500", description = "이메일 전송 실패",
                    content = @Content(schema = @Schema(implementation = ErrorResponse.class)))
    })
    @PostMapping("/new-password")
    public ResponseEntity<?> createNewPassword(@RequestBody EmailRequest emailRequest) {
        try {
            EmailMessage emailMessage = EmailMessage.builder()
                    .to(emailRequest.getEmail())
                    .subject("[서점ZIP] 임시 비밀번호 발급")
                    .build();
            passwordManagementService.sendMail(emailMessage, "password");
            memberService.setTempPassword(emailRequest.getEmail());

            return ResponseEntity.ok(
                    SuccessResponse.builder()
                            .result(true)
                            .status(HttpServletResponse.SC_OK)
                            .data(null)
                            .message("임시 비밀번호 생성 후, 메일 전송 완료")
                            .build()
            );
        }
        catch (IllegalArgumentException e){
            return ResponseEntity.badRequest().body(
                    ErrorResponse.builder()
                            .result(false)
                            .status(HttpServletResponse.SC_BAD_REQUEST)
                            .message("잘못된 요청입니다.")
                            .detail(e.getMessage())
                            .build()
            );
        }
        catch (SecurityException | AuthenticationException e) {
            return ResponseEntity.status(HttpServletResponse.SC_UNAUTHORIZED).body(
                    ErrorResponse.builder()
                            .result(false)
                            .status(HttpServletResponse.SC_UNAUTHORIZED)
                            .message("등록되지 않은 이메일입니다.")
                            .detail(e.getMessage())
                            .build()
            );
        }
        catch (Exception e){
            return ResponseEntity.status(HttpServletResponse.SC_INTERNAL_SERVER_ERROR).body(
                    ErrorResponse.builder()
                            .result(false)
                            .status(HttpServletResponse.SC_INTERNAL_SERVER_ERROR)
                            .message("서버 내부 오류가 발생했습니다.")
                            .detail(e.getMessage())
                            .build()
            );
        }
    }


}
