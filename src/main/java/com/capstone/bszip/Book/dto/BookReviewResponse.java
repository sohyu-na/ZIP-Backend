package com.capstone.bszip.Book.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.List;

@Builder
@Getter
@NoArgsConstructor
@AllArgsConstructor
public class BookReviewResponse {

    private Long bookReviewId;
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime createdAt;
    private String nickname;
    private String thumbnailUrl;
    private String title;
    private List<String> authors;
    private String publisher;
    private String isbn;
    private int rating;
    private String reviewText;


    @JsonInclude(JsonInclude.Include.NON_NULL)
    private Boolean isLiked;

    private int likesCount;


}
