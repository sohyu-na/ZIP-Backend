package com.capstone.bszip.Book.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Builder;
import lombok.Getter;

import java.util.Arrays;
import java.util.List;

@Getter
@Builder
public class UpdateBookEmbeddingRequest {
    @JsonProperty("book_id")
    String bookId;
    @JsonProperty("new_reviews")
    List<String> newReivewRequestList;

    public static UpdateBookEmbeddingRequest fromEntity(String bookId, String review) {
        List<String> reviews = Arrays.asList(review);
        return UpdateBookEmbeddingRequest.builder()
                .bookId(bookId)
                .newReivewRequestList(reviews)
                .build();
    }
}
