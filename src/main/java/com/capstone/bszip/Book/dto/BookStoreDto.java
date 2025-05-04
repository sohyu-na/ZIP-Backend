package com.capstone.bszip.Book.dto;


import com.capstone.bszip.Bookstore.domain.Bookstore;
import io.swagger.v3.oas.annotations.media.Schema;

@Schema(description = "서점 정보 DTO")
public record BookStoreDto(String bookStoreName, Long bookStoreId) {

    public static BookStoreDto from(Bookstore bookstore) {
        return new BookStoreDto(
                bookstore.getName(),
                bookstore.getBookstoreId()
        );
    }
}
