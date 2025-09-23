package com.keserugr.transaction.web;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.keserugr.transaction.dto.report.TransactionsReportRequest;
import com.keserugr.transaction.dto.report.TransactionsReportResponse;
import com.keserugr.transaction.dto.transaction.*;
import com.keserugr.transaction.service.TransactionService;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import java.time.LocalDate;

import static org.hamcrest.Matchers.is;
import static org.junit.jupiter.api.Assertions.assertEquals;
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

    //Transactions Report Test
    @Test
    void create_report_returns_200_when_dates_valid() throws Exception {
        // given
        var request = TransactionsReportRequest.builder()
                .fromDate(LocalDate.parse("2015-07-01"))
                .toDate(LocalDate.parse("2015-10-01"))
                .merchant(1)
                .build();

        var response = new TransactionsReportResponse();
        response.setStatus("APPROVED");
        when(transactionService.getTransactionReport(any())).thenReturn(response);

        // when & then
        mvc.perform(post("/api/v1/transactions/report")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.status", is("APPROVED")));

        ArgumentCaptor<TransactionsReportRequest> captor = ArgumentCaptor.forClass(TransactionsReportRequest.class);
        verify(transactionService).getTransactionReport(captor.capture());
        var passed = captor.getValue();

        assertEquals(LocalDate.parse("2015-07-01"), passed.getFromDate());
        assertEquals(LocalDate.parse("2015-10-01"), passed.getToDate());
    }

    @Test
    void create_report_returns_400_when_toDate_before_fromDate() throws Exception {
        var bad = TransactionsReportRequest.builder()
                .fromDate(LocalDate.parse("2015-10-01"))
                .toDate(LocalDate.parse("2015-07-01"))
                .merchant(1)
                .build();

        mvc.perform(post("/api/v1/transactions/report")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(bad)))
                .andExpect(status().isBadRequest());

        verifyNoInteractions(transactionService);
    }

    @Test
    void createReport_returns_400_when_validation_fails() throws Exception {
        var invalidJson = "{}";

        mvc.perform(post("/api/v1/transactions/report")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(invalidJson))
                .andExpect(status().isBadRequest());

        verifyNoInteractions(transactionService);
    }

    //Transaction List Tests
    @Test
    void transaction_list_return_200_when_dates_valid() throws Exception {
        //given
        var request = TransactionListRequest.builder()
                .fromDate(LocalDate.parse("2015-07-01"))
                .toDate(LocalDate.parse("2015-10-01"))
                .merchantId(1)
                .build();

        var response = new TransactionListResponse();
        response.setCurrentPage(1);

        when(transactionService.getTransactionList(any())).thenReturn(response);

        //when & then
        mvc.perform(post("/api/v1/transactions/list")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.current_page").value(1));

        verify(transactionService, times(1)).getTransactionList(any());
    }

    @Test
    void transaction_list_return_200_when_empty_request_was_sent() throws Exception {
        // given
        var request = new TransactionListRequest();

        var response = new TransactionListResponse();
        response.setCurrentPage(3);
        when(transactionService.getTransactionList(any())).thenReturn(response);

        // when & then
        mvc.perform(post("/api/v1/transactions/list")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.current_page").value(3));

        verify(transactionService, times(1)).getTransactionList(any());
    }

    @Test
    void transaction_list_return_400_when_toDate_before_fromDate() throws Exception {
        //given
        var bad = TransactionListRequest.builder()
                .fromDate(LocalDate.parse("2015-10-01"))
                .toDate(LocalDate.parse("2015-07-01"))
                .merchantId(1)
                .build();

        //when & then
        mvc.perform(post("/api/v1/transactions/list")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(bad)))
                .andExpect(status().isBadRequest());

        verifyNoInteractions(transactionService);
    }

    //Transaction_ById Tests
    @Test
    void get_transaction_by_id_returns_200_and_status_from_service() throws Exception {
        //given
        String transactionId = "1-1444392550-1";

        var response = new GetTransactionResponse();

        when(transactionService.getTransactionById(transactionId)).thenReturn(response);
        when(transactionService.safeStatus(response)).thenReturn("APPROVED");

        //when & then
        mvc.perform(get("/api/v1/transactions/{transactionId}", transactionId))
                .andExpect(status().isOk())
                .andExpect(content().string("APPROVED"));

        verify(transactionService).getTransactionById(transactionId);
        verify(transactionService).safeStatus(response);
        verifyNoMoreInteractions(transactionService);
    }
}