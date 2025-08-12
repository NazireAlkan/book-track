package com.nazire.booktrack.service;

import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Component;

import java.time.Duration;

@Component
public class LoginAttemptService {

    private static final int MAX_ATTEMPTS = 3;
    private static final Duration BLOCK_DURATION = Duration.ofMinutes(1);

    private final RedisTemplate<String, Integer> redisTemplate;

    public LoginAttemptService(RedisTemplate<String, Integer> redisTemplate) {
        this.redisTemplate = redisTemplate;
    }

    public void loginFailed(String email){
        String key = getKey(email);
        Integer count = redisTemplate.opsForValue().get(key);
        if(count == null) count = 0;

        count++;
        System.out.println("Yeni count değeri:" + count);

        if(count >= MAX_ATTEMPTS){
            redisTemplate.opsForValue().set(key, count, BLOCK_DURATION);
        }else {
            redisTemplate.opsForValue().set(key, count);
        }
    }

    public void loginSucceeded(String email){
        redisTemplate.delete(getKey(email));
    }

    public Boolean isBlocked(String email){
        String key = getKey(email);
        Integer count = redisTemplate.opsForValue().get(getKey(email));
        System.out.println("count değeri:" + count);
        return count != null && count >= MAX_ATTEMPTS;
    }

    public Integer count(String email){
        return redisTemplate.opsForValue().get(getKey(email));
    }


    public String getKey(String email){
        return "fail:" + email;
    }
}
