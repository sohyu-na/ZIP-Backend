package com.capstone.bszip.Bookie.dto.response;

import lombok.Builder;
import lombok.Getter;

import java.util.List;

@Getter
@Builder
public class MemberChatResponses {
    List<MemberChatHistoryResponse> chat;

    public static MemberChatResponses fromChat(List<MemberChatHistoryResponse> chat) {
        return MemberChatResponses.builder()
                .chat(chat)
                .build();
    }
}
