package com.capstone.bszip.Bookstore.service.dto;

import com.capstone.bszip.Book.dto.BookSearchResponse;
import lombok.AllArgsConstructor;
import lombok.Data;

import java.util.List;
@Data
@AllArgsConstructor
public class BookstoreDetailWithBooks {
    private BookstoreDetailResponse bookstoreDetail;
    private List<BookSearchResponse.IndepBook> bookList;
}
