package com.keserugr.transaction.service;

import com.keserugr.transaction.client.ApiClient;
import com.keserugr.transaction.dto.report.TransactionsReportRequest;
import com.keserugr.transaction.dto.report.TransactionsReportResponse;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDate;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class ReportServiceTest {

    @Mock
    ApiClient apiClient;

    @InjectMocks
    ReportService reportService;

    @Test
    void should_return_success_when_get_report() {
        //given
        TransactionsReportRequest request = TransactionsReportRequest.builder()
                .fromDate(LocalDate.parse("2015-07-01"))
                .toDate(LocalDate.parse("2015-10-01"))
                .merchant(1)
                .build();

        TransactionsReportResponse expected = new TransactionsReportResponse();
        expected.setStatus("APPROVED");

        when(apiClient.transactionsReport(request)).thenReturn(expected);

        //when
        TransactionsReportResponse out = reportService.getReport(request);

        //then
        assertSame(expected, out);
        verify(apiClient, times(1)).transactionsReport(request);
        verifyNoMoreInteractions(apiClient);
    }

    @Test
    void getReport_propagates_exceptions_from_apiClient() {
        // given
        TransactionsReportRequest request = TransactionsReportRequest.builder().build();
        RuntimeException boom = new RuntimeException("downstream fail");
        when(apiClient.transactionsReport(request)).thenThrow(boom);

        // when & then
        org.junit.jupiter.api.Assertions.assertThrows(RuntimeException.class,
                () -> reportService.getReport(request));

        verify(apiClient).transactionsReport(request);
    }


}