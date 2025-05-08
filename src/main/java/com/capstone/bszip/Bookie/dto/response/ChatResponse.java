package com.capstone.bszip.Bookie.dto.response;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Builder;
import lombok.Getter;

import java.util.List;

@Getter
@Builder
@Schema(description = "대화 응답 dto")
public class ChatResponse {
    String message;
    List<RecommendedBook> books;

    public static ChatResponse fromAPIResponse(String message, List<RecommendedBook> books) {
        return ChatResponse.builder()
                .message(message)
                .books(books)
                .build();
    }
}
