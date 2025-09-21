package com.keserugr.transaction.config;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;

@Data
@ConfigurationProperties(prefix = "reporting-api")
public class ReportingApiProperties {
    private String baseUrl;
    private String email;
    private String password;
    private String token;
}
