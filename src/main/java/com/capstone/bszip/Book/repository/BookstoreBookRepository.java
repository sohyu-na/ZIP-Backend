package com.capstone.bszip.Book.repository;

import com.capstone.bszip.Book.domain.Book;
import com.capstone.bszip.Book.domain.BookstoreBook;
import com.capstone.bszip.Bookstore.domain.Bookstore;
import io.lettuce.core.dynamic.annotation.Param;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface BookstoreBookRepository extends JpaRepository<BookstoreBook, Long> {

    boolean existsByBookAndBookstore(Book book, Bookstore bookstore);

    @Query("SELECT bb.book FROM BookstoreBook bb WHERE bb.bookstore.bookstoreId = :bookstoreId")
    List<Book> findBooksByBookstoreId(@Param("bookstoreId") Long bookstoreId);

}
