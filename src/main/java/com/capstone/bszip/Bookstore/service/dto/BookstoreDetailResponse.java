package com.capstone.bszip.Bookstore.service.dto;

import com.capstone.bszip.Bookstore.domain.Bookstore;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;

@Getter
@AllArgsConstructor
@Builder
public class BookstoreDetailResponse {
    private Long bookstoreId;
    private String name;
    private String phone;
    private Hours hours;
    private double rating;
    private String keyword;
    private String address;
    private String description;
    private boolean liked;
    private int likedCount;

    public static BookstoreDetailResponse from(Bookstore bookstore,String phone, Hours finalHours,String keyword, boolean isLiked,  int likedCount) {
        return BookstoreDetailResponse.builder()
                .bookstoreId(bookstore.getBookstoreId())
                .name(bookstore.getName())
                .phone(phone)
                .hours(finalHours)
                .rating(bookstore.getRating())
                .keyword(keyword)
                .address(bookstore.getAddress().substring(8))
                .description(bookstore.getDescription())
                .liked(isLiked)
                .likedCount(likedCount)
                .build();
    }
}
