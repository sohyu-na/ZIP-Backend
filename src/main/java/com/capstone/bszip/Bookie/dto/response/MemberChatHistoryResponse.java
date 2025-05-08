package com.capstone.bszip.Bookie.dto.response;

import com.capstone.bszip.Bookie.domain.BookieChat;
import lombok.Builder;
import lombok.Getter;

import java.time.LocalDateTime;

@Getter
@Builder
public class MemberChatHistoryResponse {
    String question;
    String answer;
    LocalDateTime createdAt;

    public static MemberChatHistoryResponse fromEntity(BookieChat chat) {
        return MemberChatHistoryResponse.builder()
                .question(chat.getQuestion())
                .answer(chat.getAnswer())
                .createdAt(chat.getCreatedDate())
                .build();
    }
}
