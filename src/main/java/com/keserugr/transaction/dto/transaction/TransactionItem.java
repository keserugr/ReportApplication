package com.keserugr.transaction.dto.transaction;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Builder
@Data
@AllArgsConstructor
@NoArgsConstructor
@JsonInclude(JsonInclude.Include.NON_NULL)
public class TransactionItem {
    private Fx fx;
    private CustomerInfo customerInfo;
    private Merchant merchant;
    private Ipn ipn;
    private Transaction transaction;
    private Acquirer acquirer;
    private Boolean refundable;
}

