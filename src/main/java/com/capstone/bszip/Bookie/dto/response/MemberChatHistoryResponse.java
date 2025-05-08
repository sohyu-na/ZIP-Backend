package com.capstone.bszip.Bookie.dto.response;

import com.capstone.bszip.Bookie.domain.BookieChat;
import jakarta.validation.constraints.NotBlank;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;

import java.time.LocalDateTime;
import java.util.List;

@Getter
@Builder
@AllArgsConstructor(access = AccessLevel.PRIVATE)
public class MemberChatHistoryResponse {
    @NotBlank
    String text;
    @NotBlank
    SpeakerType type;
    List<RecommendedBook> books;
    LocalDateTime createdAt;

    public static MemberChatHistoryResponse fromUserMessage(BookieChat chat) {
        return MemberChatHistoryResponse.builder()
                .text(chat.getQuestion())
                .type(SpeakerType.user)
                .createdAt(chat.getCreatedDate())
                .build();
    }
}
