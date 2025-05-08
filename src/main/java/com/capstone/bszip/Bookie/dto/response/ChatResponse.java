package com.capstone.bszip.Bookie.dto.response;

import lombok.Builder;
import lombok.Getter;

import java.util.List;

@Getter
@Builder
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
