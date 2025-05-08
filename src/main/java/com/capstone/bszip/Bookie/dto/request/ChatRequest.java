package com.capstone.bszip.Bookie.dto.request;

import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class ChatRequest {
    String message;

    public ChatRequest fromMessage(String message) {
        return ChatRequest.builder().message(message).build();
    }
}
