package com.capstone.bszip.Book.dto;

import com.capstone.bszip.Book.domain.BookReview;
import com.capstone.bszip.Member.domain.Member;
import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class ProfileUpdateRequest {
    Long userId;
    String userName;
    String title;
    int rating;
    String reviewText;

    public static ProfileUpdateRequest fromEntity(Member member, String bookTitle, BookReview review){
        return ProfileUpdateRequest.builder()
                .userId(member.getMemberId())
                .userName(member.getNickname())
                .title(bookTitle)
                .rating(review.getBookRating())
                .reviewText(review.getBookReviewText())
                .build();
    }

}
