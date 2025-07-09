package com.capstone.bszip.Member.service.dto;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class LoginResponse {
    private String nickname;
    private String accessToken;
    private String refreshToken;
}
