package com.keserugr.transaction.service;

import com.keserugr.transaction.client.ApiClient;
import com.keserugr.transaction.client.AuthApiClient;
import com.keserugr.transaction.config.ReportingApiProperties;
import com.keserugr.transaction.dto.login.LoginResponse;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.ValueOperations;

import java.time.Duration;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class TokenServiceTest {

    @Mock
    AuthApiClient apiClient;

    @Mock
    ReportingApiProperties properties;

    @Mock
    RedisTemplate<String, String> redis;

    @Mock
    ValueOperations<String, String> valueOperations;

    @InjectMocks
    TokenService tokenService;

    private static final String TOKEN_KEY = "TOKEN_KEY";

    @BeforeEach
    void setUp() {
        when(properties.getToken()).thenReturn(TOKEN_KEY);
    }

    @Test
    void should_login_and_cache_when_token_absent() {
        //given
        when(redis.opsForValue()).thenReturn(valueOperations);

        when(properties.getEmail()).thenReturn("email");
        when(properties.getPassword()).thenReturn("pass");

        when(valueOperations.get(properties.getToken())).thenReturn(null);
        when(apiClient.login(properties.getEmail(), properties.getPassword()))
                .thenReturn(loginResp());

        //when
        String token = tokenService.getValidToken();

        //then
        assertEquals("JWT-TOKEN", token);
        verify(apiClient, times(1))
                .login("email", "pass");
        verify(valueOperations)
                .set(eq(TOKEN_KEY), eq("JWT-TOKEN"), any(Duration.class));
    }

    @Test
    void should_return_cache_without_login() {
        //given
        when(redis.opsForValue()).thenReturn(valueOperations);
        when(valueOperations.get(properties.getToken())).thenReturn("JWT-TOKEN");

        //when
        String token = tokenService.getValidToken();

        //then
        assertEquals("JWT-TOKEN", token);
        verify(apiClient, never()).login(anyString(), anyString());
        verify(valueOperations, never())
                .set(anyString(), anyString(), any(Duration.class));
    }

    @Test
    void should_cache_with_ttl_of_9min30sec(){
        //given
        when(properties.getEmail()).thenReturn("email");
        when(properties.getPassword()).thenReturn("pass");
        when(redis.opsForValue()).thenReturn(valueOperations);
        when(valueOperations.get(TOKEN_KEY)).thenReturn(null);
        when(apiClient.login("email", "pass")).thenReturn(loginResp());

        //when
        tokenService.getValidToken();

        //then
        ArgumentCaptor<Duration> captor = ArgumentCaptor.forClass(Duration.class);
        verify(valueOperations).set(eq(TOKEN_KEY), eq("JWT-TOKEN"), captor.capture());

        Duration duration = captor.getValue();
        assertEquals(Duration.ofMinutes(9).plusSeconds(30), duration);

    }

    @Test
    void should_delete_key_when_token_invalidate(){
        //when
        tokenService.invalidateToken();

        //then
        verify(redis).delete(TOKEN_KEY);
    }

    private LoginResponse loginResp() {
        return new LoginResponse("JWT-TOKEN", "APPROVED");
    }

}