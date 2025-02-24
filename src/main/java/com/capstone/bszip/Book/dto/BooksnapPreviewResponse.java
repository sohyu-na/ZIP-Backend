package com.capstone.bszip.Book.dto;

import com.capstone.bszip.Book.repository.BooksnapPreviewDto;
import lombok.Builder;
import lombok.Getter;

import java.util.List;

@Getter
@Builder
public class BooksnapPreviewResponse {
    List<?> booksnapPreview;
    int totalPages;
    boolean last;
    Long totalElements;
}
