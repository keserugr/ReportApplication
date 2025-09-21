package com.keserugr.transaction.web;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.keserugr.transaction.dto.transaction.GetTransactionResponse;
import com.keserugr.transaction.dto.transaction.TransactionListRequest;
import com.keserugr.transaction.dto.transaction.TransactionListResponse;
import com.keserugr.transaction.service.TransactionService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import java.time.LocalDate;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(TransactionResource.class)
class TransactionResourceTest {
    @Autowired
    MockMvc mvc;

    @Autowired
    ObjectMapper objectMapper;

    @MockitoBean
    TransactionService transactionService;

    @Test
    void search_return_200_when_Dates_valid() throws Exception {
        //given
        var request = TransactionListRequest.builder()
                .fromDate(LocalDate.parse("2015-07-01"))
                .toDate(LocalDate.parse("2015-10-01"))
                .merchantId(1)
                .build();

        var response = new TransactionListResponse();
        response.setCurrent_page(1);

        when(transactionService.searchTransactions(any())).thenReturn(response);

        //when & then
        mvc.perform(post("/api/v1/transactions/search")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.current_page").value(1));

        verify(transactionService, times(1)).searchTransactions(any());
    }

    @Test
    void search_return_400_when_toDate_before_fromDate() throws Exception {
        //given
        var bad = TransactionListRequest.builder()
                .fromDate(LocalDate.parse("2015-10-01"))
                .toDate(LocalDate.parse("2015-07-01"))
                .merchantId(1)
                .build();

        //when & then
        mvc.perform(post("/api/v1/transactions/search")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(bad)))
                .andExpect(status().isBadRequest());

        verifyNoInteractions(transactionService);
    }

    @Test
    void getTransactionStatus_returns200_andStatusFromService() throws Exception {
        //given
        String transactionId = "1-1444392550-1";

        var response = new GetTransactionResponse();

        when(transactionService.getTransactionById(transactionId)).thenReturn(response);
        when(transactionService.safeStatus(response)).thenReturn("APPROVED");

        //when & then
        mvc.perform(get("/api/v1/transactions/{transactionId}/status", transactionId))
                .andExpect(status().isOk())
                .andExpect(content().string("APPROVED"));

        verify(transactionService).getTransactionById(transactionId);
        verify(transactionService).safeStatus(response);
        verifyNoMoreInteractions(transactionService);
    }
}