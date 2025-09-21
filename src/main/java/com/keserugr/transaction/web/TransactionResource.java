package com.keserugr.transaction.web;

import com.keserugr.transaction.dto.transaction.GetTransactionResponse;
import com.keserugr.transaction.dto.transaction.TransactionListRequest;
import com.keserugr.transaction.dto.transaction.TransactionListResponse;
import com.keserugr.transaction.service.TransactionService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/transactions")
@RequiredArgsConstructor
@Validated
public class TransactionResource {

    private final TransactionService transactionService;

    @PostMapping("/search")
    public ResponseEntity<TransactionListResponse> search(@Valid @RequestBody TransactionListRequest req) {

        if (req.getFromDate() != null && req.getToDate() != null
                && req.getToDate().isBefore(req.getFromDate())) {
            return ResponseEntity.badRequest().build();
        }
        return ResponseEntity.ok(transactionService.searchTransactions(req));
    }

    @GetMapping("/{transactionId}/status")
    public ResponseEntity<String> getTransactionStatus(@PathVariable String transactionId) {
        GetTransactionResponse resp = transactionService.getTransactionById(transactionId);
        String status = transactionService.safeStatus(resp);
        return ResponseEntity.ok(status);
    }
}
