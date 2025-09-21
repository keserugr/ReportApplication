package com.keserugr.transaction.web;

import com.keserugr.transaction.service.TokenService;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;


@WebMvcTest(AuthResource.class)
class AuthResourceTest {

    private static final String JWT_TOKEN = "JWT-TOKEN";

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private TokenService tokenService; // gerçek bean yerine mock

    @Test
    void getToken_returnsTokenFromService() throws Exception {
        // given
        when(tokenService.getValidToken()).thenReturn(JWT_TOKEN);

        // when + then
        mockMvc.perform(get("/api/v1/auth/token")
                        .accept(MediaType.TEXT_PLAIN))
                .andExpect(status().isOk())
                .andExpect(content().string(JWT_TOKEN));

        Mockito.verify(tokenService).getValidToken();
    }
}