package com.keserugr.transaction.dto.report;

import lombok.AllArgsConstructor;
import lombok.Data;

import java.io.Serializable;

@Data
@AllArgsConstructor
public class CurrencySummary implements Serializable {
    private Integer count;
    private Long total;
    private String currency;
}
