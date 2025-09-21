package com.keserugr.transaction.web;

import com.keserugr.transaction.dto.client.CustomerInfo;
import com.keserugr.transaction.dto.client.GetClientResponse;
import com.keserugr.transaction.service.ClientService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;


@WebMvcTest(ClientResource.class)
class ClientResourceTest {
    @Autowired
    MockMvc mockMvc;

    @MockitoBean
    ClientService clientService;

    @Test
    void getClient_returnsFullResponse() throws Exception {
        // given
        GetClientResponse response = new GetClientResponse();
        CustomerInfo info = new CustomerInfo();
        info.setEmail("test@example.com");
        response.setCustomerInfo(info);

        when(clientService.getClientByTransactionId("12345")).thenReturn(response);

        // when + then
        mockMvc.perform(get("/api/v1/clients/12345")
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.customerInfo.email").value("test@example.com"));
    }

    @Test
    void getClientEmail_returnsEmail() throws Exception {
        // given
        GetClientResponse response = new GetClientResponse();
        CustomerInfo info = new CustomerInfo();
        info.setEmail("safe@example.com");
        response.setCustomerInfo(info);

        when(clientService.getClientByTransactionId("abcde")).thenReturn(response);
        when(clientService.safeClientEmail(response)).thenReturn("safe@example.com");

        // when + then
        mockMvc.perform(get("/api/v1/clients/abcde/email"))
                .andExpect(status().isOk())
                .andExpect(content().string("safe@example.com"));
    }
}