package com.keserugr.transaction.dto.transaction;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonInclude;
import jakarta.validation.constraints.Min;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;

import java.io.Serializable;
import java.time.LocalDate;

@Builder
@Data
@AllArgsConstructor
@JsonInclude(JsonInclude.Include.NON_NULL)
public class TransactionListRequest implements Serializable {
    @JsonFormat(pattern = "yyyy-MM-dd")
    private LocalDate fromDate;

    @JsonFormat(pattern = "yyyy-MM-dd")
    private LocalDate toDate;

    private Status status;
    private Operation operation;
    private Integer merchantId;
    private Integer acquirerId;
    private PaymentMethod paymentMethod;
    private String errorCode;
    private FilterField filterField;
    private String filterValue;

    @Min(1)
    private Integer page;
}
