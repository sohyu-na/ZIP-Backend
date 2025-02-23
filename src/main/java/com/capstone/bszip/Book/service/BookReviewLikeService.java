package com.capstone.bszip.Book.service;

import com.capstone.bszip.Book.domain.BookReview;
import com.capstone.bszip.Book.domain.BookReviewLikes;
import com.capstone.bszip.Book.dto.BookReviewResponse;
import com.capstone.bszip.Book.repository.BookReviewLikesRepository;
import com.capstone.bszip.Book.repository.BookReviewRepository;
import com.capstone.bszip.Member.domain.Member;
import jakarta.persistence.EntityNotFoundException;
import jakarta.transaction.Transactional;
import lombok.extern.slf4j.Slf4j;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

import java.time.Duration;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

@Service
public class BookReviewLikeService {
    private BookReviewLikesRepository bookReviewLikesRepository;
    private BookReviewRepository bookReviewRepository;
    private final RedisTemplate<String, Object> redisTemplate;

    private static final String BOOK_REVIEW_LIKES_KEY = "book_review_likes:";
    private static final String BOOK_REVIEW_LIKES_ZEST = "book_review_likesZSet";

    public BookReviewLikeService(BookReviewLikesRepository bookReviewLikesRepository, BookReviewRepository bookReviewRepository, RedisTemplate<String, Object> redisTemplate) {
        this.bookReviewLikesRepository = bookReviewLikesRepository;
        this.bookReviewRepository = bookReviewRepository;
        this.redisTemplate = redisTemplate;
    }

    @Transactional
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

    @Transactional
    public void deleteLike(BookReviewLikes bookReviewLikes) {
        try{
            bookReviewLikesRepository.delete(bookReviewLikes);
        }catch (Exception e){
            throw new RuntimeException(e);
        }

    }

}
