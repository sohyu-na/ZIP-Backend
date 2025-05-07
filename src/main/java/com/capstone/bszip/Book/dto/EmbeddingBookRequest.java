package com.capstone.bszip.Book.dto;

import com.capstone.bszip.Book.domain.Book;
import lombok.Builder;
import lombok.Getter;

import java.util.List;

@Getter
@Builder
public class EmbeddingBookRequest {
    Long bookId;
    String title;
    String bookImageUrl;
    List<String> authors;
    BookType bookType;
    String description;

    public static EmbeddingBookRequest fromEntity(Book book) {
        return EmbeddingBookRequest.builder()
                .bookId(book.getBookId())
                .authors(book.getAuthors())
                .bookImageUrl(book.getBookImageUrl())
                .bookType(book.getBookType())
                .description(book.getContent())
                .build();
    }
}
