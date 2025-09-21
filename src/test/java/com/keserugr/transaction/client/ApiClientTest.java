package com.keserugr.transaction.client;

import com.github.tomakehurst.wiremock.client.WireMock;
import com.keserugr.transaction.dto.report.TransactionsReportRequest;
import com.keserugr.transaction.dto.report.TransactionsReportResponse;
import com.keserugr.transaction.dto.transaction.GetTransactionResponse;
import com.keserugr.transaction.dto.transaction.TransactionListRequest;
import com.keserugr.transaction.dto.transaction.TransactionListResponse;
import com.keserugr.transaction.service.TokenService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.web.reactive.function.client.WebClient;

import java.time.LocalDate;

import static com.github.tomakehurst.wiremock.client.WireMock.*;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class ApiClientTest extends WiremockBase{

    private static final String JWT_TOKEN = "JWT_TOKEN";
    private static final String TRANSACTION_LIST_PAYLOAD = """
        {
          "per_page":50,
          "current_page":1,
          "next_page_url":"http://reporting.rpdpymnt.com/api/v3/transaction/list/?page=2",
          "prev_page_url":null,
          "from":1,
          "to":50,
          "data":[
            {
              "fx":{"merchant":{"originalAmount":5,"originalCurrency":"EUR"}},
              "customerInfo":{
                "number":"448574XXXXXX3395",
                "email":"aykut.aras@bumin.com.tr",
                "billingFirstName":"Aykut",
                "billingLastName":"Aras"
              },
              "merchant":{"id":3,"name":"Dev-Merchant"},
              "ipn":{"received":true},
              "transaction":{
                "merchant":{
                  "referenceNo":"api_560a4a9314208",
                  "status":"APPROVED",
                  "operation":"3DAUTH",
                  "message":"Auth3D is APPROVED",
                  "created_at":"2015-09-29 08:24:42",
                  "transactionId":"2827-1443515082-3"
                }
              },
              "acquirer":{"id":12,"name":"Mergen Bank","code":"MB","type":"CREDITCARD"},
              "refundable":true
            }
          ]
        }
        """;

    private ApiClient client;
    private TokenService tokenService;

    @BeforeEach
    void setUp() {
        WireMock.configureFor("localhost", wm.port());
        wm.resetAll();

        WebClient webClient = WebClient.builder()
                .baseUrl("http://localhost:" + wm.port())
                .defaultHeader(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON_VALUE)
                .build();

        tokenService = mock(TokenService.class);
        client = new ApiClient(webClient, tokenService);

    }

    private TransactionsReportRequest transactionsReportRequest() {
        return TransactionsReportRequest.builder()
                .fromDate(LocalDate.parse("2025-09-19"))
                .toDate(LocalDate.parse("2025-09-21"))
                .merchant(1)
                .build();
    }

    private TransactionListRequest transactionListRequest() {
        return TransactionListRequest.builder()
                .fromDate(LocalDate.parse("2015-07-01"))
                .toDate(LocalDate.parse("2015-10-01"))
                .merchantId(1)
                .build();
    }

    @Test
    void transactionReport_shouldReturnApproved_andNotInvalidate() {
        //given
        when(tokenService.getValidToken()).thenReturn(JWT_TOKEN);

        String ok = """
            {"status":"APPROVED","response":[{"count":10,"total":1000,"currency":"GBP"}]}
        """;
        wm.stubFor(post(urlEqualTo("/api/v3/transactions/report"))
                .withHeader("Authorization", equalTo(JWT_TOKEN))
                .willReturn(aResponse()
                        .withStatus(200)
                        .withHeader("Content-Type", "application/json")
                        .withBody(ok)));

        //when
        TransactionsReportResponse resp = client.transactionsReport(transactionsReportRequest());

        //then
        assertNotNull(resp);
        assertEquals("APPROVED", resp.getStatus());
        assertEquals(1, resp.getResponse().size());
        verify(tokenService, never()).invalidateToken();
    }

    @Test
    void transactionsReport_401_thenInvalidate_thenRetry_success(){
        //given
        when(tokenService.getValidToken()).thenReturn("OLD", "NEW");

        wm.stubFor(post(urlEqualTo("/api/v3/transactions/report"))
                .withHeader("Authorization", equalTo("OLD"))
                .willReturn(aResponse().withStatus(401)));

        String ok = """
            {"status":"APPROVED","response":[{"count":1,"total":100,"currency":"EUR"}]}
        """;
        wm.stubFor(post(urlEqualTo("/api/v3/transactions/report"))
                .withHeader("Authorization", equalTo("NEW"))
                .willReturn(aResponse()
                        .withStatus(200)
                        .withHeader("Content-Type", "application/json")
                        .withBody(ok)));

        //when
        TransactionsReportResponse resp = client.transactionsReport(transactionsReportRequest());

        //then
        assertNotNull(resp);
        assertEquals("APPROVED", resp.getStatus());
        verify(tokenService, times(1)).invalidateToken();

        verify(1, postRequestedFor(urlEqualTo("/api/v3/transactions/report"))
                .withHeader("Authorization", equalTo("OLD")));
        verify(1, postRequestedFor(urlEqualTo("/api/v3/transactions/report"))
                .withHeader("Authorization", equalTo("NEW")));
    }

    @Test
    void transactionsReport_shouldSendAuthHeader_andJsonBody() {
        //given
        when(tokenService.getValidToken()).thenReturn(JWT_TOKEN);

        wm.stubFor(post(urlEqualTo("/api/v3/transactions/report"))
                .withHeader("Authorization", equalTo(JWT_TOKEN))
                .withRequestBody(matchingJsonPath("$.fromDate", equalTo("2025-09-19")))
                .withRequestBody(matchingJsonPath("$.toDate", equalTo("2025-09-21")))
                .withRequestBody(matchingJsonPath("$.merchant", equalTo("1")))
                .willReturn(aResponse()
                        .withStatus(200)
                        .withHeader("Content-Type", "application/json")
                        .withBody("""
                                {"status":"APPROVED","response":[]}
                                """)));

        //when
        var resp = client.transactionsReport(transactionsReportRequest());

        //then
        assertEquals("APPROVED", resp.getStatus());

        verify(postRequestedFor(urlEqualTo("/api/v3/transactions/report"))
                .withHeader("Authorization", equalTo(JWT_TOKEN))
                .withRequestBody(matchingJsonPath("$.fromDate", equalTo("2025-09-19")))
                .withRequestBody(matchingJsonPath("$.toDate", equalTo("2025-09-21")))
                .withRequestBody(matchingJsonPath("$.merchant", equalTo("1"))));
    }

    @Test
    void transactionList_return_success() {
        when(tokenService.getValidToken()).thenReturn(JWT_TOKEN);

        wm.stubFor(post(urlEqualTo("/api/v3/transaction/list"))
                .withHeader("Authorization", equalTo(JWT_TOKEN))
                // body kontratı (örnek): fromDate/toDate/merchant alanları
                .withRequestBody(matchingJsonPath("$.fromDate", equalTo("2015-07-01")))
                .withRequestBody(matchingJsonPath("$.toDate", equalTo("2015-10-01")))
                .willReturn(aResponse()
                        .withStatus(200)
                        .withHeader("Content-Type","application/json")
                        .withBody(TRANSACTION_LIST_PAYLOAD)));

        //when
        TransactionListResponse response = client.transactionList(transactionListRequest());

        //then
        assertNotNull(response);
        assertEquals(50, response.getPer_page());
        assertEquals(1, response.getCurrent_page());
        assertEquals(1, response.getFrom());
        assertEquals(50, response.getTo());
        assertNotNull(response.getData());
        assertEquals(1, response.getData().size());

        var row = response.getData().get(0);
        assertEquals(5.0, row.getFx().getMerchant().getOriginalAmount(), 0.0001);
        assertEquals("EUR", row.getFx().getMerchant().getOriginalCurrency());
        assertEquals("Aykut", row.getCustomerInfo().getBillingFirstName());
        assertEquals("Dev-Merchant", row.getMerchant().getName());
        assertTrue(row.getIpn().getReceived());
        assertEquals("api_560a4a9314208", row.getTransaction().getMerchant().getReferenceNo());
        assertEquals("MB", row.getAcquirer().getCode());
        assertTrue(row.getRefundable());

        verify(postRequestedFor(urlEqualTo("/api/v3/transaction/list"))
                .withHeader("Authorization", equalTo(JWT_TOKEN)));

    }

    @Test
    void transactionList_401_thenInvalidate_thenRetry_success() {

        //given
        when(tokenService.getValidToken()).thenReturn("OLD", "NEW");

        wm.stubFor(post(urlEqualTo("/api/v3/transaction/list"))
                .withHeader("Authorization", equalTo("OLD"))
                .willReturn(aResponse().withStatus(401)));

        String ok = """
        {"status":"APPROVED","per_page":50,"current_page":1,"from":1,"to":1,"data":[]}
        """;
        wm.stubFor(post(urlEqualTo("/api/v3/transaction/list"))
                .withHeader("Authorization", equalTo("NEW"))
                .willReturn(aResponse()
                        .withStatus(200)
                        .withHeader("Content-Type","application/json")
                        .withBody(TRANSACTION_LIST_PAYLOAD)));

        TransactionListResponse response = client.transactionList(transactionListRequest());

        assertNotNull(response);
        assertEquals(50, response.getPer_page());
        assertEquals(1, response.getCurrent_page());
        assertEquals(1, response.getFrom());
        assertEquals(50, response.getTo());
        assertNotNull(response.getData());
        assertEquals(1, response.getData().size());

        verify(tokenService, times(1)).invalidateToken();

        verify(1, postRequestedFor(urlEqualTo("/api/v3/transaction/list"))
                .withHeader("Authorization", equalTo("OLD")));
        verify(1, postRequestedFor(urlEqualTo("/api/v3/transaction/list"))
                .withHeader("Authorization", equalTo("NEW")));
    }

    @Test
    void getTransaction_shouldSendTransactionIdJson_andReturnApproved() {
        //given
        when(tokenService.getValidToken()).thenReturn(JWT_TOKEN);

        String transactionId = "1-1444392550-1";

        String payload = """
            {
              "fx": { "merchant": { "originalAmount": 100, "originalCurrency": "EUR" } },
              "customerInfo": { "id": "1" },
              "merchant": { "name": "Dev-Merchant" },
              "transaction": { "merchant": { "transactionId": "1-1444392550-1" } }
            }
        """;

        wm.stubFor(post(urlEqualTo("/api/v3/transaction"))
                .withHeader("Authorization", equalTo(JWT_TOKEN))
                .withRequestBody(matchingJsonPath("$.transactionId", equalTo(transactionId)))
                .willReturn(aResponse()
                        .withStatus(200)
                        .withHeader("Content-Type","application/json")
                        .withBody(payload)));

        //when
        GetTransactionResponse response = client.getTransaction(transactionId);

        //then
        assertNotNull(response);
        assertEquals("EUR", response.getFx().getMerchant().getOriginalCurrency());
        assertEquals(1, response.getCustomerInfo().getId());
        assertEquals("Dev-Merchant", response.getMerchant().getName());
        assertEquals("1-1444392550-1", response.getTransaction().getMerchant().getTransactionId());

        verify(postRequestedFor(urlEqualTo("/api/v3/transaction"))
                .withHeader("Authorization", equalTo(JWT_TOKEN))
                .withRequestBody(matchingJsonPath("$.transactionId", equalTo(transactionId))));
    }
}