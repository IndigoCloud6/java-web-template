package com.example.template.controller;

import com.example.template.common.response.ApiResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.HashMap;
import java.util.Map;

/**
 * API Hello Controller (JWT Protected)
 * Requires JWT token for access
 */
@Tag(name = "API", description = "JWT Protected API Endpoints")
@RestController
@RequestMapping("/api")
public class ApiHelloController {

    @Operation(
            summary = "API Hello",
            description = "Hello endpoint protected by JWT authentication",
            security = @SecurityRequirement(name = "Bearer Authentication")
    )
    @GetMapping("/hello")
    public ApiResponse<Map<String, Object>> hello() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        
        Map<String, Object> data = new HashMap<>();
        data.put("message", "Hello from API endpoint!");
        data.put("user", authentication.getName());
        data.put("authorities", authentication.getAuthorities());
        data.put("authenticationType", "JWT");
        
        return ApiResponse.success(data);
    }

}
