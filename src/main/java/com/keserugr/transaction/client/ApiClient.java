package com.keserugr.transaction.client;

import com.keserugr.transaction.dto.client.GetClientResponse;
import com.keserugr.transaction.dto.login.LoginRequest;
import com.keserugr.transaction.dto.login.LoginResponse;
import com.keserugr.transaction.dto.report.TransactionsReportRequest;
import com.keserugr.transaction.dto.report.TransactionsReportResponse;
import com.keserugr.transaction.dto.transaction.GetTransactionResponse;
import com.keserugr.transaction.dto.transaction.TransactionIdBody;
import com.keserugr.transaction.dto.transaction.TransactionListRequest;
import com.keserugr.transaction.dto.transaction.TransactionListResponse;
import com.keserugr.transaction.service.TokenService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpHeaders;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;
import org.springframework.web.reactive.function.client.WebClientResponseException;

import java.time.Duration;
import java.util.function.Supplier;

@Service
@RequiredArgsConstructor
public class ApiClient {
    private final WebClient webClient;
    private final TokenService tokenService;

    public LoginResponse login(String email, String password) {
        return webClient.post()
                .uri("/api/v3/merchant/user/login")
                .bodyValue(new LoginRequest(email, password))
                .retrieve()
                .bodyToMono(LoginResponse.class)
                .timeout(Duration.ofSeconds(7))
                .block();
    }

    public TransactionsReportResponse transactionsReport(TransactionsReportRequest request) {
        return doWithRetry(
                () -> postWithAuth(
                        "/api/v3/transactions/report",
                        request,
                        TransactionsReportResponse.class));
    }

    public TransactionListResponse transactionList(TransactionListRequest request) {
        return doWithRetry(
                () -> postWithAuth(
                        "/api/v3/transaction/list",
                        request,
                        TransactionListResponse.class));
    }

    public GetTransactionResponse getTransaction(String transactionId) {
        return doWithRetry(
                () -> postWithAuth(
                        "/api/v3/transaction",
                        new TransactionIdBody(transactionId),
                        GetTransactionResponse.class));
    }

    public GetClientResponse getClient(String transactionId) {
        return doWithRetry(
                () -> postWithAuth(
                        "/api/v3/client",
                        new TransactionIdBody(transactionId),
                        GetClientResponse.class));
    }

    private <T> T postWithAuth(String path, Object body, Class<T> type) {
        String token = tokenService.getValidToken();
        return webClient.post()
                .uri(path)
                .header(HttpHeaders.AUTHORIZATION, token)
                .bodyValue(body)
                .retrieve()
                .bodyToMono(type)
                .timeout(Duration.ofSeconds(7))
                .block();
    }

    private <T> T doWithRetry(Supplier<T> call) {
        try {
            return call.get();
        } catch (WebClientResponseException.Unauthorized e) {
            tokenService.invalidateToken();
            return call.get();
        }
    }
}