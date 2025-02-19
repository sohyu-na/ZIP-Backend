package com.capstone.bszip.Book.service;

import com.capstone.bszip.Book.domain.BookReview;
import com.capstone.bszip.Book.domain.BookReviewLikes;
import com.capstone.bszip.Book.repository.BookReviewLikesRepository;
import com.capstone.bszip.Book.repository.BookReviewRepository;
import jakarta.persistence.EntityNotFoundException;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.stereotype.Service;

@Service
public class BookReviewLikeService {
    private BookReviewLikesRepository bookReviewLikesRepository;
    private BookReviewRepository bookReviewRepository;

    public BookReviewLikeService(BookReviewLikesRepository bookReviewLikesRepository, BookReviewRepository bookReviewRepository) {
        this.bookReviewLikesRepository = bookReviewLikesRepository;
        this.bookReviewRepository = bookReviewRepository;
    }

    public void saveLike(BookReviewLikes bookReviewLikes) {
        try{
            bookReviewLikesRepository.save(bookReviewLikes);
        }catch (DataIntegrityViolationException e){
            throw new DataIntegrityViolationException("무결성 제약 조건 위반: ", e);
        } catch (Exception e){
            throw new RuntimeException("알수 없는 에러");
        }
    }

}
