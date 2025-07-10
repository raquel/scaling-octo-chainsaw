
package com.example.usermanagement.controller;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.time.LocalDateTime;
import java.util.Map;

@RestController
@RequestMapping("/external-api")
public class ExternalApiController {

    @GetMapping("/data")
    public Map<String, Object> getFakeExternalData() {
        return Map.of(
            "message", "This is fake external API data",
            "timestamp", LocalDateTime.now(),
            "data", Map.of(
                "users_count", 150,
                "active_sessions", 45,
                "system_status", "healthy"
            )
        );
    }

    @GetMapping("/health")
    public Map<String, Object> getHealth() {
        return Map.of(
            "status", "UP",
            "timestamp", LocalDateTime.now(),
            "service", "External API Mock"
        );
    }
}
