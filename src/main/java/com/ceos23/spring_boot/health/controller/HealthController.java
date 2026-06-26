package com.ceos23.spring_boot.health.controller;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class HealthController {

    @GetMapping("/api/v1/health/ready")
    public String health() {
        return "OK";
    }
}