package com.keserugr.transaction.client;

import com.keserugr.transaction.dto.login.LoginRequest;
import com.keserugr.transaction.dto.login.LoginResponse;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;

import java.time.Duration;

@Service
@AllArgsConstructor
public class AuthApiClient {

    private final WebClient webClient;

    public LoginResponse login(String email, String password) {
        return webClient.post()
                .uri("/api/v3/merchant/user/login")
                .bodyValue(new LoginRequest(email, password))
                .retrieve()
                .bodyToMono(LoginResponse.class)
                .timeout(Duration.ofSeconds(7))
                .block();
    }
}
