package com.odontoconnect.common;

import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;

@RestController
@RequestMapping("/api/v1/status")
@SecurityRequirement(name = "bearerAuth")
public class StatusController {

    @GetMapping
    public Map<String, String> status() {
        return Map.of(
                "application", "OdontoConnect API",
                "status", "online",
                "environment", "development"
        );
    }
}
