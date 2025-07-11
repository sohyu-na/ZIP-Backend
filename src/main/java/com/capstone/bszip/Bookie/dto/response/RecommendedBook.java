package com.capstone.bszip.Bookie.dto.response;

import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
@AllArgsConstructor(access = AccessLevel.PRIVATE)
public class RecommendedBook {
    String title;
    String bookId;
    String bookImageUrl;

    public static RecommendedBook fromJsonProperty(String title, String bookId, String bookImageUrl){
        return RecommendedBook.builder()
                .title(title)
                .bookId(bookId)
                .bookImageUrl(bookImageUrl)
                .build();
    }
}
