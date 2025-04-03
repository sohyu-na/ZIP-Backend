package com.capstone.bszip.Bookstore.service.dto;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class Hours {
    private String weekday;  // 평일
    private String saturday; // 토요일
    private String sunday;   // 일요일
}