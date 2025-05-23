package com.capstone.bszip.Book.dto;

import com.capstone.bszip.Book.domain.BookReview;
import com.capstone.bszip.Member.domain.Member;
import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;

import java.time.ZoneId;
import java.util.Date;

@Getter
@Builder
@AllArgsConstructor(access = AccessLevel.PRIVATE)
public class BooksnapPreviewDto {
    private Long bookReviewId;
    private String userName;
    private Date createdAt;
    private int like;
    private String review;


    @JsonInclude(JsonInclude.Include.NON_NULL)
    private Boolean isLiked;

    private int rating;

    BookInfoDto bookInfo;

    public static BooksnapPreviewDto fromBookReview(BookReview bookReview, Member member) {
        return BooksnapPreviewDto.builder()
                .bookReviewId(bookReview.getBookReviewId())
                .userName(bookReview.getMember().getNickname())
                .createdAt(Date.from(bookReview.getCreatedAt().atZone(ZoneId.systemDefault()).toInstant()))
                .review(bookReview.getBookReviewText())
                .like(bookReview.getBookReviewLikesList().size())
                .isLiked(member != null && bookReview.getBookReviewLikesList().stream()
                        .anyMatch(like -> like.getMember().getMemberId().equals(member.getMemberId())))
                .rating(bookReview.getBookRating())
                .bookInfo(BookInfoDto.fromEntity(bookReview.getBook()))
                .build();

    }

}
