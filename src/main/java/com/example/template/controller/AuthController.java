package com.example.template.controller;

import com.example.template.common.response.ApiResponse;
import com.example.template.dto.request.LoginRequest;
import com.example.template.dto.response.JwtResponse;
import com.example.template.security.jwt.JwtTokenUtil;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpSession;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.web.context.HttpSessionSecurityContextRepository;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.Map;

/**
 * Authentication Controller
 * Provides both JWT and Session authentication
 */
@Slf4j
@Tag(name = "Authentication", description = "Authentication API")
@RestController
@RequestMapping("/auth")
@RequiredArgsConstructor
public class AuthController {

    private final AuthenticationManager authenticationManager;
    private final JwtTokenUtil jwtTokenUtil;

    /**
     * JWT Login - Returns JWT token for /api/** endpoints
     */
    @Operation(summary = "JWT Login", description = "Authenticate and get JWT token for API access")
    @PostMapping("/jwt/login")
    public ApiResponse<JwtResponse> jwtLogin(@Valid @RequestBody LoginRequest request) {
        // Authenticate user
        Authentication authentication = authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(request.getUsername(), request.getPassword())
        );

        // Generate JWT token
        UserDetails userDetails = (UserDetails) authentication.getPrincipal();
        String token = jwtTokenUtil.generateToken(userDetails);

        JwtResponse response = new JwtResponse(token, userDetails.getUsername());
        return ApiResponse.success(response);
    }

    /**
     * Session Login - Creates session for /admin/** endpoints
     */
    @Operation(summary = "Session Login", description = "Authenticate and create session for admin access")
    @PostMapping("/session/login")
    public ApiResponse<Map<String, Object>> sessionLogin(
            @Valid @RequestBody LoginRequest request,
            HttpServletRequest httpRequest) {
        
        // Authenticate user
        Authentication authentication = authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(request.getUsername(), request.getPassword())
        );

        // Create security context
        SecurityContext securityContext = SecurityContextHolder.createEmptyContext();
        securityContext.setAuthentication(authentication);
        SecurityContextHolder.setContext(securityContext);

        // Create session
        HttpSession session = httpRequest.getSession(true);
        session.setAttribute(HttpSessionSecurityContextRepository.SPRING_SECURITY_CONTEXT_KEY, securityContext);

        Map<String, Object> data = new HashMap<>();
        data.put("username", authentication.getName());
        data.put("sessionId", session.getId());
        
        log.info("Session login successful for user: {}, sessionId: {}", authentication.getName(), session.getId());
        
        return ApiResponse.success("Login successful", data);
    }

    /**
     * Session Logout
     */
    @Operation(summary = "Session Logout", description = "Logout and invalidate session")
    @PostMapping("/session/logout")
    public ApiResponse<Void> sessionLogout(HttpServletRequest request) {
        HttpSession session = request.getSession(false);
        if (session != null) {
            session.invalidate();
        }
        SecurityContextHolder.clearContext();
        return ApiResponse.success("Logout successful", null);
    }

    /**
     * Get current user info
     */
    @Operation(summary = "Current User", description = "Get current authenticated user information")
    @GetMapping("/me")
    public ApiResponse<Map<String, Object>> currentUser() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        
        Map<String, Object> data = new HashMap<>();
        data.put("username", authentication.getName());
        data.put("authorities", authentication.getAuthorities());
        
        return ApiResponse.success(data);
    }

}
