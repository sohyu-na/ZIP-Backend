package com.capstone.bszip.Bookie.dto.request;

import com.capstone.bszip.Member.domain.Member;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class APIChatRequest {
    @JsonProperty("user_id")
    String userId;
    @JsonProperty("user_name")
    String userName;
    @JsonProperty("message")
    String message;

    public static APIChatRequest fromEntity(Member member, ChatRequest chatRequest) {
        return APIChatRequest.builder()
                .userId(member.getMemberId().toString())
                .userName(member.getNickname())
                .message(chatRequest.getMessage())
                .build();
    }
}
