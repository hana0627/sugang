package com.hana.sugang.api.course.repository.redis;

import lombok.RequiredArgsConstructor;
import org.springframework.data.redis.core.RedisCallback;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Repository;

@Repository
@RequiredArgsConstructor
public class CourseRedisRepository {

    private final RedisTemplate<String, String> redisTemplate;


    public void flushAll() {
        redisTemplate.execute((RedisCallback<Object>) connection -> {
            connection.flushAll();
            return null;
        });
    }



    public Long increment(String code) {
        return redisTemplate.opsForValue()
                .increment("course_"+code);
    }

    /**
     * //TODO
     * RestAPI 다채화
     * 수강신청 취소시 활용
     */
    public Long decrement(String code) {
        return redisTemplate.opsForValue()
                .decrement("course_"+code);
    }
}
