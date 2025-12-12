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
 * Admin Hello Controller (Session Protected)
 * Requires session authentication for access
 */
@Tag(name = "Admin", description = "Session Protected Admin Endpoints")
@RestController
@RequestMapping("/admin")
public class AdminHelloController {

    @Operation(
            summary = "Admin Hello",
            description = "Hello endpoint protected by Session authentication",
            security = @SecurityRequirement(name = "Session Authentication")
    )
    @GetMapping("/hello")
    public ApiResponse<Map<String, Object>> hello() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        
        Map<String, Object> data = new HashMap<>();
        data.put("message", "Hello from Admin endpoint!");
        data.put("user", authentication.getName());
        data.put("authorities", authentication.getAuthorities());
        data.put("authenticationType", "Session");
        
        return ApiResponse.success(data);
    }

}
