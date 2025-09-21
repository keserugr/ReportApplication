package com.keserugr.transaction.dto.report;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class TransactionsReportResponse implements Serializable {
    private String status;
    private List<CurrencySummary> response;
}
