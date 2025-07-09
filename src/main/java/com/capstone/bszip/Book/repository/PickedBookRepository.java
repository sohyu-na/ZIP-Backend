package com.capstone.bszip.Book.repository;

import com.capstone.bszip.Book.domain.Book;
import com.capstone.bszip.Book.domain.PickedBook;
import com.capstone.bszip.Member.domain.Member;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

public interface PickedBookRepository extends JpaRepository<PickedBook, Long> {
    Boolean existsByBookAndMember(Book book, Member member);

    Optional<PickedBook> findByBookAndMember(Book book, Member member);

    @Query("SELECT pb.book FROM PickedBook pb WHERE pb.member = :member")
    List<Book> findBooksByMember(@Param("member") Member member);
}
