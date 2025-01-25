package com.capstone.bszip.Member.service.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
//@Builder
public class SignupRequest {
    private String email;
    private String password;
}
