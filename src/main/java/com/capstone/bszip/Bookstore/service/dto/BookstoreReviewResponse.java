package com.capstone.bszip.Bookstore.service.dto;

import com.capstone.bszip.Bookstore.domain.BookstoreReview;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;

import java.time.LocalDateTime;

@Getter
@AllArgsConstructor
@Builder
public class BookstoreReviewResponse {
    private Long bookstoreReviewId;
    private String nickname;
    private double rating;
    private String text;
    private String imageUrl;
    private LocalDateTime createdAt;

    public static BookstoreReviewResponse from(BookstoreReview review) {
        return BookstoreReviewResponse.builder()
                .bookstoreReviewId(review.getBookstoreReviewId())
                .nickname(review.getMember().getNickname())
                .rating(review.getRating())
                .text(review.getText())
                .imageUrl(review.getImageUrl())
                .createdAt(review.getCreatedAt())
                .build();
    }
}
