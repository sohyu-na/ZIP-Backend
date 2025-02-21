package com.capstone.bszip.Book.repository;

import com.capstone.bszip.Book.domain.BookReview;
import com.capstone.bszip.Member.domain.Member;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface BookReviewRepository extends JpaRepository<BookReview, Long> {
    Optional<BookReview> findBookReviewByBookReviewId(Long bookReviewId);
    Optional<BookReview> findBookReviewsByBookReviewIdAndMember(Long bookReviewId, Member member);
    boolean existsBookReviewByBookReviewId(Long bookReviewId);
}
