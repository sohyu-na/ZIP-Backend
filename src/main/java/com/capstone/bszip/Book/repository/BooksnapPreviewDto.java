package com.capstone.bszip.Book.repository;

import com.capstone.bszip.Book.dto.BookInfoDto;
import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.Builder;
import lombok.Getter;

import java.util.Date;

@Getter
@Builder
public class BooksnapPreviewDto {
    private String userName;
    private Date createdAt;
    private String like;
    private String review;


    @JsonInclude(JsonInclude.Include.NON_NULL)
    private Boolean isLiked;

    private int rating;

    BookInfoDto bookInfo;

}
