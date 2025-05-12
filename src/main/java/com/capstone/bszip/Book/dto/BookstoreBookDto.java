package com.capstone.bszip.Book.dto;

import com.capstone.bszip.Book.domain.BookstoreBook;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
@AllArgsConstructor
public class BookstoreBookDto {
    private BookStoreDto bookstore;

    public static BookstoreBookDto fromEntity(BookstoreBook entity) {
        return BookstoreBookDto.builder()
                .bookstore(BookStoreDto.from(entity.getBookstore()))
                .build();
    }
}