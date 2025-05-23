package com.capstone.bszip.Book.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

import java.util.List;

@Getter
@Builder
@AllArgsConstructor
@Schema(description = "찜한 책 목록 응답")
public class MyPickBooksResponse {
    @Schema(description = "찜한 책 목록")
    List<MyPickBookResponse> pickBooks;

    public static MyPickBooksResponse toMyPickBooksResponse(List<MyPickBookResponse> myPickBookResponses) {
        return MyPickBooksResponse.builder()
                .pickBooks(myPickBookResponses)
                .build();
    }

}
