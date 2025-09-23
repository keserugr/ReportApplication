package com.keserugr.transaction.service;

import com.keserugr.transaction.client.ApiClient;
import com.keserugr.transaction.dto.report.TransactionsReportRequest;
import com.keserugr.transaction.dto.report.TransactionsReportResponse;
import com.keserugr.transaction.dto.transaction.*;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDate;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class TransactionServiceTest {

    @Mock
    ApiClient apiClient;

    @InjectMocks
    TransactionService transactionService;

    @Test
    void should_return_success_when_get_transaction_report() {
        //given
        TransactionsReportRequest request = TransactionsReportRequest.builder()
                .fromDate(LocalDate.parse("2015-07-01"))
                .toDate(LocalDate.parse("2015-10-01"))
                .merchant(1)
                .build();

        var expected = new TransactionsReportResponse();
        expected.setStatus("APPROVED");

        when(apiClient.transactionsReport(request)).thenReturn(expected);

        //when
        TransactionsReportResponse out = transactionService.getTransactionReport(request);

        //then
        assertSame(expected, out);
        verify(apiClient, times(1)).transactionsReport(request);
        verifyNoMoreInteractions(apiClient);
    }

    @Test
    void should_throw_exception_when_get_transaction_report_failed() {
        // given
        TransactionsReportRequest request = TransactionsReportRequest.builder().build();
        RuntimeException boom = new RuntimeException("downstream fail");
        when(apiClient.transactionsReport(request)).thenThrow(boom);

        // when & then
        assertThrows(RuntimeException.class,
                () -> transactionService.getTransactionReport(request));
        verify(apiClient).transactionsReport(request);
    }

    //Transaction List Tests

    @Test
    void getTransactionList_delegates_and_returns_response_with_non_null_data() {
        // given
        var request = TransactionListRequest.builder()
                .fromDate(LocalDate.parse("2015-07-01"))
                .toDate(LocalDate.parse("2015-10-01"))
                .merchantId(1)
                .build();

        var response = new TransactionListResponse();
        response.setCurrentPage(1);
        response.setData(List.of(
                TransactionItem.builder()
                        .customerInfo(CustomerInfo.builder()
                                .id(1).build()).build()));

        when(apiClient.transactionList(request)).thenReturn(response);

        // when
        var result = transactionService.getTransactionList(request);

        // then
        assertSame(response, result);
        verify(apiClient).transactionList(request);
        verifyNoMoreInteractions(apiClient);
    }

    @Test
    void should_return_transaction_list_handles_null_data_without_exception() {
        // given
        var request = TransactionListRequest.builder().build();
        var resp = new TransactionListResponse();
        resp.setCurrentPage(1);
        resp.setData(null);
        when(apiClient.transactionList(request)).thenReturn(resp);

        // when
        var out = transactionService.getTransactionList(request);

        // then
        assertSame(resp, out);
        verify(apiClient).transactionList(request);
    }

    //Transaction ById Tests

    @Test
    void should_return_transaction_by_id_success() {
        // given
        String transactionId = "1-1444392550-1";
        var response = new GetTransactionResponse();
        response.setCustomerInfo(CustomerInfo.builder().id(1).build());
        when(apiClient.getTransaction(transactionId)).thenReturn(response);

        // when
        var result = transactionService.getTransactionById(transactionId);

        // then
        assertSame(response, result);
        verify(apiClient).getTransaction(transactionId);
        verifyNoMoreInteractions(apiClient);
    }

    @Test
    void should_throw_exception_when_get_transaction_by_id_failed() {
        // given
        String transactionId = "bad";
        when(apiClient.getTransaction(transactionId)).thenThrow(new RuntimeException("401/404"));

        // when / then
        assertThrows(RuntimeException.class, () -> transactionService.getTransactionById(transactionId));
        verify(apiClient).getTransaction(transactionId);
    }

    //safeStatus Tests

    @Test
    void safeStatus_returns_status_when_success() {
        // given
        var transactionMerchant = new Transaction.TransactionMerchant();
        transactionMerchant.setStatus("APPROVED");
        var transaction = new Transaction();
        transaction.setMerchant(transactionMerchant);
        var resp = new GetTransactionResponse();
        resp.setTransaction(transaction);

        // when
        String status = transactionService.safeStatus(resp);

        // then
        assertEquals("APPROVED", status);
    }

    @Test
    void safeStatus_returns_UNKNOWN_when_failed() {
        // given / when / then
        assertEquals("UNKNOWN", transactionService.safeStatus(null));

        var r1 = new GetTransactionResponse();
        r1.setTransaction(null);
        assertEquals("UNKNOWN", transactionService.safeStatus(r1));

        var r2 = new GetTransactionResponse();
        var t2 = new Transaction();
        t2.setMerchant(null);
        r2.setTransaction(t2);
        assertEquals("UNKNOWN", transactionService.safeStatus(r2));

        var r3 = new GetTransactionResponse();
        var t3 = new Transaction();
        var tm3 = new Transaction.TransactionMerchant();
        tm3.setStatus(null);
        t3.setMerchant(tm3);
        r3.setTransaction(t3);
        assertEquals("UNKNOWN", transactionService.safeStatus(r3));
    }


}