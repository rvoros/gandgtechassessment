package com.rvoros.fileprocessor.configuration;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.client.RestClient;

@Configuration
public class AppConfig {
    @Bean
    @Value("${ipApiBaseUrl}")
    public RestClient ipApiRestClient(String ipApiBaseUrl) {
        return RestClient.builder()
                .baseUrl(ipApiBaseUrl)
                .build();
    }
}
