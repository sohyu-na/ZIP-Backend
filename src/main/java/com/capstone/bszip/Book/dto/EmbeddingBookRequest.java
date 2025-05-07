package com.capstone.bszip.Book.dto;

import com.capstone.bszip.Book.domain.Book;
import lombok.Builder;
import lombok.Getter;

import java.util.List;

@Getter
@Builder
public class EmbeddingBookRequest {
    String bookId;
    String title;
    String bookImageUrl;
    String bookType;
    String description;

    public static EmbeddingBookRequest fromEntity(Book book) {
        String content = book.getContent();
        if(content == null || content.isEmpty()){
            content = "";
        }
        return EmbeddingBookRequest.builder()
                .bookId(book.getBookId().toString())
                .bookImageUrl(book.getBookImageUrl())
                .bookType(book.getBookType().toString())
                .description(content)
                .build();
    }
}
