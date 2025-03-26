package com.capstone.bszip.Book.repository;

import com.capstone.bszip.Book.domain.Book;
import com.capstone.bszip.Book.dto.BookType;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface BookRepository extends JpaRepository<Book, Long> {
    boolean existsByBookId(Long bookId);
    Optional<Book> findByBookId(Long bookId);
    Page<Book> findByBookNameContainingAndBookType(String bookName, BookType bookType, Pageable pageable);
}
