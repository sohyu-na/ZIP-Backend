package com.capstone.bszip.Book.dto;

import com.capstone.bszip.Book.domain.Book;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;

import java.util.List;

@Getter
@Builder
@AllArgsConstructor(access = AccessLevel.PRIVATE)
public class BookInfoDto {
    private String bookId;
    private String title;
    private String bookImageUrl;
    private List<String> authors;
    private String publisher;
    private List<?> bookStores;
    private BookType bookType;

    public static BookInfoDto fromEntity(Book book) {
        return BookInfoDto.builder()
                .bookId(book.getBookId().toString())
                .title(book.getBookName())
                .bookImageUrl(book.getBookImageUrl())
                .bookStores(book.getBookstoreBookList().stream()
                        .map(BookstoreBookDto::fromEntity)
                        .toList())
                .authors(book.getAuthors())
                .bookType(book.getBookType())
                .build();
    }
}
