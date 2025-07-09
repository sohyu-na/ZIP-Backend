package com.capstone.bszip.Bookstore;

import lombok.RequiredArgsConstructor;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class BookstoreLikeTrendingScheduler {

    private final RedisTemplate<String,String> redisTemplate;

    @Scheduled(cron="0 0 0 * * MON")
    public void resetWeeklyTrend(){
        // 백업
        redisTemplate.rename("trending:bookstores:weekly", "trending:bookstores:lastweek");
        // 초기화
        redisTemplate.delete("trending:bookstores:weekly");
    }
}
