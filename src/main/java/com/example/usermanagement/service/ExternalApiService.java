
package com.example.usermanagement.service;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.time.LocalDateTime;
import java.util.Map;

@Service
public class ExternalApiService {

    @Value("${external.api.url:http://localhost:8080/external-api/data}")
    private String externalApiUrl;

    @Value("${external.api.interval:60000}")
    private long intervalMs;

    private final RestTemplate restTemplate = new RestTemplate();

    @Scheduled(fixedRateString = "${external.api.interval:60000}")
    public void callExternalApiPeriodically() {
        try {
            System.out.println("Calling external API at: " + LocalDateTime.now());
            String response = callExternalApi();
            System.out.println("External API response: " + response);
        } catch (Exception e) {
            System.err.println("Error calling external API: " + e.getMessage());
        }
    }

    public String callExternalApi() {
        try {
            return restTemplate.getForObject(externalApiUrl, String.class);
        } catch (Exception e) {
            throw new RuntimeException("Failed to call external API: " + e.getMessage());
        }
    }

    public Map<String, Object> getExternalApiStatus() {
        return Map.of(
            "url", externalApiUrl,
            "interval", intervalMs,
            "lastCall", LocalDateTime.now(),
            "status", "active"
        );
    }
}
