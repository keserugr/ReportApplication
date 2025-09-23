package com.keserugr.transaction.service;

import com.keserugr.transaction.client.ApiClient;
import com.keserugr.transaction.dto.report.TransactionsReportRequest;
import com.keserugr.transaction.dto.report.TransactionsReportResponse;
import com.keserugr.transaction.dto.transaction.*;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
@Slf4j
public class TransactionService {
    private final ApiClient apiClient;

    public TransactionsReportResponse getTransactionReport(TransactionsReportRequest req) {
        return apiClient.transactionsReport(req);
    }

    public TransactionListResponse getTransactionList(TransactionListRequest req) {
        TransactionListResponse resp = apiClient.transactionList(req);

        if (resp.getData() != null) {
            long approvedCount = resp.getData().stream()
                    .map(TransactionItem::getTransaction)
                    .filter(t -> t != null && t.getMerchant() != null)
                    .map(m -> m.getMerchant().getStatus())
                    .filter("APPROVED"::equalsIgnoreCase)
                    .count();
            log.debug("Approved count : {}", approvedCount);
        }
        return resp;
    }

    public GetTransactionResponse getTransactionById(String transactionId) {
        return apiClient.getTransaction(transactionId);
    }

    public String safeStatus(GetTransactionResponse resp) {
        return java.util.Optional.ofNullable(resp)
                .map(GetTransactionResponse::getTransaction)
                .map(Transaction::getMerchant)
                .map(Transaction.TransactionMerchant::getStatus)
                .orElse("UNKNOWN");
    }
}
