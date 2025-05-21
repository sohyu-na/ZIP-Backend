package com.capstone.bszip.Book.dto;

import com.capstone.bszip.Book.domain.Book;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;

import java.util.List;

@Getter
@Builder
@AllArgsConstructor
@Schema(description = "찜한 책 목록")
public class MyPickBookResponse {
    Long bookId;
    String bookName;
    List<String> bookAuthors;
    String publisher;

    public static MyPickBookResponse from(Book book) {
        return MyPickBookResponse.builder()
                .bookName(book.getBookName())
                .bookAuthors(book.getAuthors())
                .publisher(book.getPublisher())
                .bookId(book.getBookId())
                .build();
    }
}
