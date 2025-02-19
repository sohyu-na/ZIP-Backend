package com.capstone.bszip.Book.service;

import com.capstone.bszip.Book.domain.BookReview;
import com.capstone.bszip.Book.domain.BookReviewLikes;
import com.capstone.bszip.Book.repository.BookReviewLikesRepository;
import com.capstone.bszip.Book.repository.BookReviewRepository;
import com.capstone.bszip.Member.domain.Member;
import jakarta.persistence.EntityNotFoundException;
import lombok.extern.slf4j.Slf4j;
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


    public boolean isAleadyLiked(BookReview bookReview, Member member) {
        return bookReviewLikesRepository.existsBookReviewLikesByBookReviewAndMember(bookReview, member);
    }

    public BookReviewLikes getLike(BookReview bookReview, Member member) {
        return bookReviewLikesRepository.findBookReviewLikesByBookReviewAndMember(bookReview, member).orElseThrow(()-> new EntityNotFoundException("해당 리뷰와 멤버로 리뷰를 찾을 수 없음"));
    }

    public void deleteLike(BookReviewLikes bookReviewLikes) {
        try{
            bookReviewLikesRepository.delete(bookReviewLikes);
        }catch (Exception e){
            throw new RuntimeException(e);
        }

    }

}
