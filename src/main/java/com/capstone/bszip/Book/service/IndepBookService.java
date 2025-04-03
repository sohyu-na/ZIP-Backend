package com.capstone.bszip.Book.service;

import com.capstone.bszip.Book.dto.AddIsEndBookResponse;
import com.capstone.bszip.Book.dto.BookSearchResponse;
import com.capstone.bszip.Book.dto.BookStoreDto;
import com.capstone.bszip.Book.dto.BookType;
import com.capstone.bszip.Book.repository.BookRepository;
import com.capstone.bszip.Bookstore.repository.BookstoreRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
public class IndepBookService {

    private final BookRepository bookRepository;
    private final BookstoreRepository bookstoreRepository;

    public AddIsEndBookResponse getIndepBookByBooktitle(String title, int page){
        Pageable pageable = PageRequest.of(page - 1, 10);
        Page<BookSearchResponse.IndepBook> indepBooks = bookRepository.findByBookNameContainingAndBookType(title, BookType.indep, pageable)
                .map(indepBook -> {
                    return BookSearchResponse.IndepBook.builder()
                        .bookId(indepBook.getBookId())
                        .bookImageUrl(indepBook.getBookImageUrl())
                        .authors(indepBook.getAuthors())
                        .title(indepBook.getBookName())
                        .build();}

                );
        return new AddIsEndBookResponse(indepBooks.isLast(), indepBooks.getContent());
    }

    public AddIsEndBookResponse getIndepBookByAuthor(String author, int page){
        Pageable pageable = PageRequest.of(page - 1, 10);
        Page<BookSearchResponse.IndepBook> indepBooks = bookRepository.findByAuthorsContainingAndBookType(author, BookType.indep, pageable)
                .map(indepBook -> {
                    return BookSearchResponse.IndepBook.builder()
                            .bookId(indepBook.getBookId())
                            .bookImageUrl(indepBook.getBookImageUrl())
                            .authors(indepBook.getAuthors())
                            .title(indepBook.getBookName())
                            .build();}

                );
        return new AddIsEndBookResponse(indepBooks.isLast(), indepBooks.getContent());
    }

    public List<BookStoreDto> getBookstoreIdAndName(String query) {
        if (query == null || query.trim().isEmpty()) {
            return bookstoreRepository.findTop10ByOrderByBookstoreIdDesc()
                    .stream()
                    .map(BookStoreDto::from)
                    .collect(Collectors.toList());
        }

        return bookstoreRepository.findAllByNameContaining(query)
                .stream()
                .map(BookStoreDto::from)
                .collect(Collectors.toList());
    }
}
