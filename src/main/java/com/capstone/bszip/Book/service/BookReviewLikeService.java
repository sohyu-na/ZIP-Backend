package com.capstone.bszip.Book.service;

import com.capstone.bszip.Book.domain.BookReview;
import com.capstone.bszip.Book.domain.BookReviewLikes;
import com.capstone.bszip.Book.repository.BookReviewLikesRepository;
import com.capstone.bszip.Book.repository.BookReviewRepository;
import com.capstone.bszip.Member.domain.Member;
import jakarta.persistence.EntityNotFoundException;
import jakarta.transaction.Transactional;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

import java.time.ZoneId;

@Service
public class BookReviewLikeService {
    private final BookReviewLikesRepository bookReviewLikesRepository;
    private final BookReviewRepository bookReviewRepository;
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
            double timestampWeight = bookReviewLikes.getBookReview().getCreatedAt().atZone(ZoneId.systemDefault()).toInstant().toEpochMilli() / 1_000_000_000.0;
            redisTemplate.opsForZSet().incrementScore(BOOK_REVIEW_LIKES_KEY,
                    bookReviewLikes.getId().toString(),
                    1 + timestampWeight);
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
            double timestampWeight = bookReviewLikes.getBookReview().getCreatedAt().atZone(ZoneId.systemDefault()).toInstant().toEpochMilli() / 1_000_000_000.0;
            redisTemplate.opsForZSet().incrementScore(BOOK_REVIEW_LIKES_KEY, bookReviewLikes.getId().toString(), -1 - timestampWeight);
        }catch (Exception e){
            throw new RuntimeException(e);
        }

    }

    public int getLikeCount(Long reviewId) {
        Double score = redisTemplate.opsForZSet().score(BOOK_REVIEW_LIKES_KEY, reviewId.toString());
        System.out.println("score: " + score);
        if (score != null) {
            return score.intValue();
        }

        int likeCount = bookReviewLikesRepository.countByBookReview_BookReviewId(reviewId);
        redisTemplate.opsForZSet().add(BOOK_REVIEW_LIKES_KEY, reviewId.toString(), likeCount);

        return likeCount;
    }


}
