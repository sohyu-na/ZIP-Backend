package com.capstone.bszip.Book.dto;

import com.capstone.bszip.Book.domain.BookReview;
import com.capstone.bszip.Member.domain.Member;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class ProfileUpdateRequest {
    @JsonProperty("user_id")
    String userId;
    @JsonProperty("user_name")
    String userName;
    @JsonProperty("title")
    String title;
    @JsonProperty("rating")
    int rating;
    @JsonProperty("review_text")
    String reviewText;

    public static ProfileUpdateRequest fromEntity(Member member, String bookTitle, BookReview review){
        return ProfileUpdateRequest.builder()
                .userId(member.getMemberId().toString())
                .userName(member.getNickname())
                .title(bookTitle)
                .rating(review.getBookRating())
                .reviewText(review.getBookReviewText())
                .build();
    }

}
