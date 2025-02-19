package com.capstone.bszip.Book.repository;

import com.capstone.bszip.Book.domain.BookReview;
import com.capstone.bszip.Book.domain.BookReviewLikes;
import com.capstone.bszip.Member.domain.Member;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface BookReviewLikesRepository extends JpaRepository<BookReviewLikes, Long> {
    boolean existsBookReviewLikesByBookReviewAndMember(BookReview bookReview, Member member);
}
