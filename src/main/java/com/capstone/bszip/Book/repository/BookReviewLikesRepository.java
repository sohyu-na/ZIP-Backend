package com.capstone.bszip.Book.repository;

import com.capstone.bszip.Book.domain.BookReviewLikes;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface BookReviewLikesRepository extends JpaRepository<BookReviewLikes, Long> {
}
