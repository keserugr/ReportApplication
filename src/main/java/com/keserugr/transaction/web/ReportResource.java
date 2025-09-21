package com.keserugr.transaction.web;

import com.keserugr.transaction.dto.report.TransactionsReportRequest;
import com.keserugr.transaction.dto.report.TransactionsReportResponse;
import com.keserugr.transaction.service.ReportService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1/reports")
@RequiredArgsConstructor
@Validated
public class ReportResource {

    public final ReportService reportService;

    @PostMapping
    public ResponseEntity<TransactionsReportResponse> createReport(
            @Valid @RequestBody TransactionsReportRequest request) {
        if (request.getToDate().isBefore(request.getFromDate())) {
            return ResponseEntity.badRequest().build();
        }
        return ResponseEntity.ok(reportService.getReport(request));
    }
}
