package com.capstone.bszip.Book.service;

import com.capstone.bszip.Book.repository.BookReviewLikesRepository;
import jakarta.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Map;

@Component
@RequiredArgsConstructor
public class RedisInitializer {
    private final RedisTemplate<String, Object> redisTemplate;
    private final BookReviewLikesRepository bookReviewLikesRepository;

    private static final String BOOK_REVIEW_LIKES_KEY = "book_review_likes:";

    @PostConstruct
    public void loadBookReviewLikes() {
        System.out.println("🔹 RedisInitializer 실행 중...");

        List<Object[]> likeCounts = bookReviewLikesRepository.countBookReviewLikeForAllReviews();
        for (Object[] row : likeCounts) {
            Long reviewId = (Long) row[0];
            Long likeCount = (Long) row[1];

            // Redis에 저장
            redisTemplate.opsForZSet().add(BOOK_REVIEW_LIKES_KEY, reviewId.toString(), likeCount);

            // 콘솔 출력
            System.out.println("✅ Redis 저장: reviewId=" + reviewId + ", likeCount=" + likeCount);
        }

    }
}
