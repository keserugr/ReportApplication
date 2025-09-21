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
public class ReportService {
    private final ApiClient apiClient;

    public TransactionsReportResponse getReport(TransactionsReportRequest req) {
        return apiClient.transactionsReport(req);
    }
}
