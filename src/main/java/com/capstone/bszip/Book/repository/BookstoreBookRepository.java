package com.capstone.bszip.Book.repository;

import com.capstone.bszip.Book.domain.BookstoreBook;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface BookstoreBookRepository extends JpaRepository<BookstoreBook, Long> {

}
