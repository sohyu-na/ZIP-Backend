package com.capstone.bszip.Bookie.dto.response;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Builder;
import lombok.Getter;

import java.util.List;

@Getter
@Builder
@Schema(description = "히스토리 DTO")
public class MemberChatResponses {
    List<MemberChatHistoryResponse> chat;

    public static MemberChatResponses fromChat(List<MemberChatHistoryResponse> chat) {
        return MemberChatResponses.builder()
                .chat(chat)
                .build();
    }
}
