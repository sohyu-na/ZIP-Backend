package com.capstone.bszip.Bookstore.service.dto;

import com.capstone.bszip.Bookstore.domain.Bookstore;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;

@Getter
@AllArgsConstructor
@Builder
public class BookstoreResponse {
    private Long bookstoreId;
    private String name;
    private double rating;
    private String keyword;
    private String address;
    private boolean liked;

    public static BookstoreResponse from (Bookstore bookstore,boolean isLiked){
        // address에서 우편번호 제외
        String addressExceptCode = bookstore.getAddress().substring(8);
        // 일반 -> 일반서적으로
        String keyword = bookstore.getKeyword();
        if(keyword.equals(" 일반")){
            keyword =" 일반서적";
        }

        return BookstoreResponse.builder()
                .bookstoreId(bookstore.getBookstoreId())
                .name(bookstore.getName())
                .rating(bookstore.getRating())
                .keyword(keyword)
                .address(addressExceptCode)
                .liked(isLiked)
                .build();

    }
}
