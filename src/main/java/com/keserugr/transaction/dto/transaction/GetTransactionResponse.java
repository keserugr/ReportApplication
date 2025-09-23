package com.keserugr.transaction.dto.transaction;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@JsonInclude(JsonInclude.Include.NON_NULL)
public class GetTransactionResponse {

    private Fx fx;
    private CustomerInfo customerInfo;
    private Merchant merchant;
    private Transaction transaction;
    private Status status;
    private Operation operation;
    private String errorCode;
}
