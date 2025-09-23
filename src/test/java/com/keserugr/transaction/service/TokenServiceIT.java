package com.keserugr.transaction.service;

import com.keserugr.transaction.client.ApiClient;
import com.keserugr.transaction.client.AuthApiClient;
import com.keserugr.transaction.config.ReportingApiProperties;
import com.keserugr.transaction.dto.login.LoginResponse;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.data.redis.connection.lettuce.LettuceConnectionFactory;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.serializer.StringRedisSerializer;
import org.testcontainers.containers.GenericContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;

import java.util.concurrent.TimeUnit;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;


@Testcontainers
public class TokenServiceIT {

    @Container
    static final GenericContainer<?> redisContainer = new GenericContainer<>("redis:7-alpine")
            .withExposedPorts(6379);

    private AuthApiClient apiClient;

    private RedisTemplate<String, String> redisTemplate;
    private ReportingApiProperties properties;

    private TokenService service;

    private static final String TOKEN_KEY = "TOKEN_KEY";

    @BeforeEach
    void setUp() {
        LettuceConnectionFactory cf = new LettuceConnectionFactory(
                redisContainer.getHost(), redisContainer.getMappedPort(6379));
        cf.afterPropertiesSet();

        redisTemplate = new RedisTemplate<>();
        redisTemplate.setConnectionFactory(cf);
        StringRedisSerializer stringRedisSerializer = new StringRedisSerializer();
        redisTemplate.setKeySerializer(stringRedisSerializer);
        redisTemplate.setValueSerializer(stringRedisSerializer);
        redisTemplate.setHashKeySerializer(stringRedisSerializer);
        redisTemplate.setHashValueSerializer(stringRedisSerializer);
        redisTemplate.afterPropertiesSet();

        apiClient = Mockito.mock(AuthApiClient.class);

        properties = new ReportingApiProperties();
        properties.setEmail("test@email.com");
        properties.setPassword("password");
        properties.setBaseUrl("http://localhost:6379");
        properties.setToken(TOKEN_KEY);

        service = new TokenService(apiClient, redisTemplate, properties);

        redisTemplate.delete(TOKEN_KEY);
    }

    @Test
    void caches_token_in_real_redis_when_absent_and_ttl_9min30sec(){
        //given
        when(apiClient.login("test@email.com","password"))
                .thenReturn(loginResp());

        //when
        String token = service.getValidToken();

        //then
        assertEquals(token, "JWT-TOKEN");

        String inRedis = redisTemplate.opsForValue().get(TOKEN_KEY);
        assertEquals(inRedis, "JWT-TOKEN");

        Long ttlSec = redisTemplate.getExpire(TOKEN_KEY, TimeUnit.SECONDS);
        assertNotNull(ttlSec);
        assertTrue(ttlSec > 540L);
        assertTrue(ttlSec < 600L);

        verify(apiClient, times(1))
                .login("test@email.com", "password");
    }

    @Test
    void returns_cached_token_without_login() {
        //given
        redisTemplate.opsForValue()
                .set(TOKEN_KEY, "CACHED-JWT", 300, TimeUnit.SECONDS);

        //when
        String token = service.getValidToken();

        //then
        assertEquals(token, "CACHED-JWT");
        verify(apiClient, never()).login(anyString(), anyString());
    }

    @Test
    void delete_key_in_redis_return_invalidate_token() {
        //given
        redisTemplate.opsForValue()
                .set(TOKEN_KEY, "CACHED-JWT", 300, TimeUnit.SECONDS);

        //when
        service.invalidateToken();

        //then
        String token = redisTemplate.opsForValue().get(TOKEN_KEY);
        assertNull(token);
    }

    private LoginResponse loginResp() {
        return new LoginResponse("JWT-TOKEN", "APPROVED");
    }
}
