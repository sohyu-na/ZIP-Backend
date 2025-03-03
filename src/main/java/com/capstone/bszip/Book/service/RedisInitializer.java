package com.capstone.bszip.Book.service;

import com.capstone.bszip.Book.repository.BookReviewLikesRepository;
import jakarta.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;

@Component
@RequiredArgsConstructor
@EnableScheduling
public class RedisInitializer {
    private final RedisTemplate<String, Object> redisTemplate;
    private final BookReviewLikesRepository bookReviewLikesRepository;

    private static final String BOOK_REVIEW_LIKES_KEY = "book_review_likes:";
    private static final String LAST7DAYS_BOOK_REVIEW_LIKES_KEY = "last7days_book_review_likes:";

    @PostConstruct
    public void loadBookReviewLikes() {
        System.out.println("🔹 BookReviewLikes - RedisInitializer 실행 중...");

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

    @PostConstruct
    @Scheduled(cron="0 0 0 * * ?")
    public void getLikesForBookReviewFromLast7Days(){
        // 오늘(실시간)부터 7일 전의 생성된 좋아요 데이터를 가지고 좋아요 순으로 ZSet에 저장
        System.out.println("🥦 BookReviewFromLast7Days - redisinitializer 실행 중 ...");

        LocalDateTime sevenDaysAgo = LocalDateTime.now().minusDays(7);
        List<Object []> likeCounts = bookReviewLikesRepository.countBookReviewLikeForLast7Days(sevenDaysAgo);
        for (Object[] row : likeCounts) {
            Long reviewId = (Long) row[0];
            Long likeCount = (Long) row[1];

            redisTemplate.opsForZSet().add(LAST7DAYS_BOOK_REVIEW_LIKES_KEY, reviewId.toString(), likeCount);

            System.out.println("🥦 Redis 저장 : reviewId=" + reviewId + ", likeCount=" + likeCount);
        }
    }
}
