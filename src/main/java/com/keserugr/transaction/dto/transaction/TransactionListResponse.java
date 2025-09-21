package com.keserugr.transaction.dto.transaction;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
@JsonInclude(JsonInclude.Include.NON_NULL)
public class TransactionListResponse {
    private Integer per_page;
    private Integer current_page;
    private String next_page_url;
    private String prev_page_url;
    private Integer from;
    private Integer to;
    private List<TransactionItem> data;
}

