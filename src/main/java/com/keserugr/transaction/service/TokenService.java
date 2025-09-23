package com.keserugr.transaction.service;

import com.keserugr.transaction.client.ApiClient;
import com.keserugr.transaction.client.AuthApiClient;
import com.keserugr.transaction.config.ReportingApiProperties;
import com.keserugr.transaction.dto.login.LoginResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

import java.time.Duration;
import java.time.Instant;

@Service
@RequiredArgsConstructor
public class TokenService {
    private final AuthApiClient client;
    private final RedisTemplate<String, String> redisTemplate;
    private final ReportingApiProperties properties;


    public String getValidToken() {
        String cached = redisTemplate.opsForValue().get(properties.getToken());
        if(cached != null && !cached.isEmpty()) return cached;

        LoginResponse response = client.login(properties.getEmail(), properties.getPassword());
        String token = response.getToken();
        redisTemplate.opsForValue()
                .set(properties.getToken(), token, Duration.ofMinutes(9).plusSeconds(30));
        return token;
    }

    public void invalidateToken() {
        redisTemplate.delete(properties.getToken());
    }
}
