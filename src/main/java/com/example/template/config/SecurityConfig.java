package com.example.template.config;

import com.example.template.security.filter.JwtAuthenticationFilter;
import com.example.template.security.handler.CustomAccessDeniedHandler;
import com.example.template.security.handler.CustomAuthenticationEntryPoint;
import com.example.template.security.jwt.JwtTokenUtil;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.provisioning.InMemoryUserDetailsManager;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

/**
 * Security Configuration
 * - /api/** uses JWT authentication (stateless)
 * - /admin/** uses Session authentication (stateful)
 * - Public endpoints: /health, /auth/**, /swagger-ui/**, /v3/api-docs/**, /actuator/**
 */
@Configuration
@EnableWebSecurity
@EnableMethodSecurity
@RequiredArgsConstructor
public class SecurityConfig {

    private final CustomAuthenticationEntryPoint authenticationEntryPoint;
    private final CustomAccessDeniedHandler accessDeniedHandler;
    private final JwtTokenUtil jwtTokenUtil;

    @Value("${jwt.header}")
    private String jwtHeader;

    @Value("${jwt.prefix}")
    private String jwtPrefix;

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http
                // CSRF is disabled for JWT-based API authentication (/api/**)
                // For session-based routes (/admin/**), consider enabling CSRF in production
                .csrf(AbstractHttpConfigurer::disable)
                .authorizeHttpRequests(auth -> auth
                        // Public endpoints
                        .requestMatchers(
                                "/health",
                                "/auth/**",
                                "/swagger-ui.html",
                                "/swagger-ui/**",
                                "/v3/api-docs/**",
                                "/swagger-resources/**",
                                "/webjars/**",
                                "/actuator/**"
                        ).permitAll()
                        // JWT protected endpoints (stateless)
                        .requestMatchers("/api/**").authenticated()
                        // Session protected endpoints (stateful)
                        .requestMatchers("/admin/**").authenticated()
                        // All other requests need authentication
                        .anyRequest().authenticated()
                )
                .exceptionHandling(exception -> exception
                        .authenticationEntryPoint(authenticationEntryPoint)
                        .accessDeniedHandler(accessDeniedHandler)
                )
                // Configure session management
                .securityContext(context -> context
                        .requireExplicitSave(false)
                )
                .sessionManagement(session -> session
                        // Use stateless for /api/**, stateful for /admin/**
                        .sessionCreationPolicy(SessionCreationPolicy.IF_REQUIRED)
                );

        // Add JWT filter before UsernamePasswordAuthenticationFilter
        http.addFilterBefore(jwtAuthenticationFilter(), UsernamePasswordAuthenticationFilter.class);

        return http.build();
    }

    /**
     * JWT Authentication Filter Bean
     */
    @Bean
    public JwtAuthenticationFilter jwtAuthenticationFilter() {
        return new JwtAuthenticationFilter(jwtTokenUtil, userDetailsService(), jwtHeader, jwtPrefix);
    }

    /**
     * In-memory user details service for demo purposes
     * 
     * SECURITY WARNING: This is for demonstration only!
     * In production, use a proper user store (database, LDAP, etc.)
     * and externalize credentials to environment variables or secure configuration
     */
    @Bean
    public UserDetailsService userDetailsService() {
        UserDetails apiUser = User.builder()
                .username("apiuser")
                .password(passwordEncoder().encode("apipass"))
                .roles("USER")
                .build();

        UserDetails adminUser = User.builder()
                .username("admin")
                .password(passwordEncoder().encode("adminpass"))
                .roles("ADMIN")
                .build();

        return new InMemoryUserDetailsManager(apiUser, adminUser);
    }

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    @Bean
    public AuthenticationManager authenticationManager(AuthenticationConfiguration config) throws Exception {
        return config.getAuthenticationManager();
    }

}
