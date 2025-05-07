package com.capstone.bszip.Book.dto;

import com.capstone.bszip.Book.domain.Book;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Builder;
import lombok.Getter;

import java.util.List;

@Getter
@Builder
public class EmbeddingBookRequest {
    @JsonProperty("book_id")
    String bookId;
    @JsonProperty("title")
    String title;
    @JsonProperty("book_image_url")
    String bookImageUrl;
    @JsonProperty("book_type")
    String bookType;
    @JsonProperty("description")
    String description;

    public static EmbeddingBookRequest fromEntity(Book book) {
        String content = book.getContent();
        if(content == null || content.isEmpty()){
            content = "";
        }
        return EmbeddingBookRequest.builder()
                .bookId(book.getBookId().toString())
                .title(book.getBookName())
                .bookImageUrl(book.getBookImageUrl())
                .bookType(book.getBookType().toString())
                .description(content)
                .build();
    }
}
