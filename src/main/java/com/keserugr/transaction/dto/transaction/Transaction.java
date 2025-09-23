package com.keserugr.transaction.dto.transaction;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class Transaction {
    private TransactionMerchant merchant;

    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    public static class TransactionMerchant {
        private String referenceNo;
        private String status;
        private String operation;
        private String message;
        private String created_at;
        private String transactionId;
    }
}
