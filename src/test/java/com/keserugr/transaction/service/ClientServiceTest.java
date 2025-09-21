package com.keserugr.transaction.service;

import com.keserugr.transaction.client.ApiClient;
import com.keserugr.transaction.dto.client.CustomerInfo;
import com.keserugr.transaction.dto.client.GetClientResponse;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class ClientServiceTest {

    @Mock
    ApiClient apiClient;

    @InjectMocks
    ClientService clientService;

    @Test
    void should_return_client_response_by_transaction_id() {
        //given
        String transactionId = "1-1444392550-1";
        GetClientResponse expected = new GetClientResponse();
        CustomerInfo customerInfo = new CustomerInfo();
        customerInfo.setEmail("michael@gmail.com");
        expected.setCustomerInfo(customerInfo);

        when(apiClient.getClient(transactionId)).thenReturn(expected);

        //when
        var response = clientService.getClientByTransactionId(transactionId);

        //then
        assertSame(expected, response);
        verify(apiClient, times(1)).getClient(transactionId);
        verifyNoMoreInteractions(apiClient);
    }

    @Test
    void should_throw_exception_when_transaction_id_is_wrong() {
        //given
        String transactionId = "bad-id";
        when(apiClient.getClient(transactionId)).thenThrow(new RuntimeException("downstream error"));

        //when & then
        assertThrows(RuntimeException.class, () -> clientService.getClientByTransactionId(transactionId));
        verify(apiClient).getClient(transactionId);
    }

    @Test
    void safeClientEmail_returns_email_when_present() {
        //given
        GetClientResponse response = new GetClientResponse();
        CustomerInfo customerInfo = new CustomerInfo();
        customerInfo.setEmail("michael@gmail.com");
        response.setCustomerInfo(customerInfo);

        //when
        String email = clientService.safeClientEmail(response);

        //then
        assertEquals("michael@gmail.com", email);
    }

    @Test
    void safeClientEmail_returns_NA_when_customerInfo_is_null() {
        //given
        GetClientResponse response = new GetClientResponse();
        response.setCustomerInfo(null);

        //when
        String email = clientService.safeClientEmail(response);

        //then
        assertEquals("N/A", email);
    }

    @Test
    void safeClientEmail_returns_NA_when_response_is_null() {
        //when
        String email = clientService.safeClientEmail(null);

        //then
        assertEquals("N/A", email);
    }

}