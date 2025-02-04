package com.capstone.bszip.Member.controller;

import com.capstone.bszip.Member.service.PasswordManagementService;
import com.capstone.bszip.Member.service.dto.EmailMessage;
import com.capstone.bszip.Member.service.dto.EmailRequest;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/new-password")
public class PasswordManagementController {
    private final PasswordManagementService passwordManagementService;
    public PasswordManagementController(PasswordManagementService passwordManagementService) {
        this.passwordManagementService = passwordManagementService;
    }

    @PostMapping("/")
    public ResponseEntity<?> createNewPassword(@RequestBody EmailRequest emailRequest) {
        EmailMessage emailMessage = EmailMessage.builder()
                .to(emailRequest.getEmail())
                .subject("[서점ZIP] 임시 비밀번호 발급")
                .build();
        passwordManagementService.sendMail(emailMessage, "password");

        return ResponseEntity.ok().build();

    }


}
