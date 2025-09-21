package com.keserugr.transaction.web;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.keserugr.transaction.dto.report.TransactionsReportRequest;
import com.keserugr.transaction.dto.report.TransactionsReportResponse;
import com.keserugr.transaction.service.ReportService;
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
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;


@WebMvcTest(ReportResource.class)
class ReportResourceTest {

    @Autowired
    MockMvc mvc;
    @Autowired
    ObjectMapper om;

    @MockitoBean
    ReportService reportService;

    @Test
    void createReport_returns_200_when_dates_valid() throws Exception {
        // given
        var request = TransactionsReportRequest.builder()
                .fromDate(LocalDate.parse("2015-07-01"))
                .toDate(LocalDate.parse("2015-10-01"))
                .merchant(1)
                .build();

        var response = new TransactionsReportResponse();
        response.setStatus("APPROVED");
        when(reportService.getReport(any())).thenReturn(response);

        // when & then
        mvc.perform(post("/api/v1/reports")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(om.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.status", is("APPROVED")));

        ArgumentCaptor<TransactionsReportRequest> captor = ArgumentCaptor.forClass(TransactionsReportRequest.class);
        verify(reportService).getReport(captor.capture());
        var passed = captor.getValue();

        assertEquals(LocalDate.parse("2015-07-01"), passed.getFromDate());
        assertEquals(LocalDate.parse("2015-10-01"), passed.getToDate());
    }

    @Test
    void createReport_returns_400_when_toDate_before_fromDate() throws Exception {
        var bad = TransactionsReportRequest.builder()
                .fromDate(LocalDate.parse("2015-10-01"))
                .toDate(LocalDate.parse("2015-07-01"))
                .merchant(1)
                .build();

        mvc.perform(post("/api/v1/reports")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(om.writeValueAsString(bad)))
                .andExpect(status().isBadRequest());

        verifyNoInteractions(reportService);
    }

    @Test
    void createReport_returns_400_when_validation_fails() throws Exception {
        var invalidJson = "{}";

        mvc.perform(post("/api/v1/reports")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(invalidJson))
                .andExpect(status().isBadRequest());

        verifyNoInteractions(reportService);
    }
}