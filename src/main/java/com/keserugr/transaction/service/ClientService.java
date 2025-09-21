package com.keserugr.transaction.service;

import com.keserugr.transaction.client.ApiClient;
import com.keserugr.transaction.dto.client.CustomerInfo;
import com.keserugr.transaction.dto.client.GetClientResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class ClientService {
    private final TokenService tokenService;
    private final ApiClient apiClient;

    public GetClientResponse getClientByTransactionId(String transactionId) {
        return apiClient.getClient(transactionId);
    }

    public String safeClientEmail(GetClientResponse res) {
        return java.util.Optional.ofNullable(res)
                .map(GetClientResponse::getCustomerInfo)
                .map(CustomerInfo::getEmail)
                .orElse("N/A");
    }
}
