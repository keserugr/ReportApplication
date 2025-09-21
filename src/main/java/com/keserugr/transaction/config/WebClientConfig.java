package com.keserugr.transaction.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.MediaType;
import org.springframework.web.reactive.function.client.WebClient;

import org.springframework.http.HttpHeaders;

@Configuration
public class WebClientConfig {

    @Bean
    public WebClient webClient(ReportingApiProperties reportingApiProperties) {
        return WebClient.builder()
                .baseUrl(reportingApiProperties.getBaseUrl())
                .defaultHeader(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON_VALUE)
                .build();
    }
}
