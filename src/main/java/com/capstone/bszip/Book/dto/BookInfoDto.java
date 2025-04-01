package com.capstone.bszip.Book.dto;

import lombok.Builder;
import lombok.Getter;

import java.util.List;

@Getter
@Builder
public class BookInfoDto {
    private String bookId;
    private String title;
    private String bookImageUrl;
    private List<String> authors;
    private String publisher;
    private List<?> bookStores;
}
