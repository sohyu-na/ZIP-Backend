package com.capstone.bszip.Bookstore.service.dto;

import com.capstone.bszip.Bookstore.domain.Hashtag;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;

@Getter
@AllArgsConstructor
@Builder
public class HashtagResponse {
    private String tag;
    private Long bookstoreId;

    public static HashtagResponse from(Hashtag hashtag) {
        return HashtagResponse.builder()
                .tag(hashtag.getTag())
                .bookstoreId(hashtag.getBookstore().getBookstoreId())
                .build();
    }
}
