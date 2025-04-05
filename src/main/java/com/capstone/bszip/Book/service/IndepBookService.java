package com.capstone.bszip.Book.service;

import com.capstone.bszip.Book.domain.Book;
import com.capstone.bszip.Book.dto.AddIsEndBookResponse;
import com.capstone.bszip.Book.dto.BookSearchResponse;
import com.capstone.bszip.Book.dto.BookType;
import com.capstone.bszip.Book.repository.BookRepository;
import com.capstone.bszip.Book.repository.BookstoreBookRepository;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
@AllArgsConstructor
@Slf4j
public class IndepBookService {

    private final BookRepository bookRepository;
    private final BookstoreBookRepository bookstoreBookRepository;

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

    //서점에서 보유한 책 검색
    public List<BookSearchResponse.IndepBook> getIndepBookByBookstore(long bookstoreId){
        List<Book> books = bookstoreBookRepository.findBooksByBookstoreId(bookstoreId);

        return books.stream()
                .map(book -> BookSearchResponse.IndepBook.builder()
                        .bookId(book.getBookId())
                        .bookImageUrl(book.getBookImageUrl())
                        .authors(book.getAuthors())
                        .title(book.getBookName())
                        .build())
                .collect(Collectors.toList());
    }

}
