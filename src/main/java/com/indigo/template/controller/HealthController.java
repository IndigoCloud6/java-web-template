package com.indigo.template.controller;

import com.indigo.template.common.response.ApiResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;

/**
 * Health Check Controller (Public)
 */
@Tag(name = "Health", description = "Health Check API")
@RestController
@RequestMapping("/health")
public class HealthController {

    @Operation(summary = "Health Check", description = "Check if the service is running")
    @GetMapping
    public ApiResponse<Map<String, Object>> health() {
        Map<String, Object> data = new HashMap<>();
        data.put("status", "UP");
        data.put("timestamp", LocalDateTime.now());
        data.put("service", "java-web-template");
        return ApiResponse.success(data);
    }

}
