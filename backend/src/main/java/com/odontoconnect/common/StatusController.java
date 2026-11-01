package com.odontoconnect.common;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;

@RestController
@RequestMapping("/api/v1/status")
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
