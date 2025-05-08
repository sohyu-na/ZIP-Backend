package com.capstone.bszip.Bookie.dto.request;

import lombok.*;

@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor(access = AccessLevel.PRIVATE)
public class ChatRequest {
    String message;

    public ChatRequest fromMessage(String message) {
        return ChatRequest.builder().message(message).build();
    }
}
