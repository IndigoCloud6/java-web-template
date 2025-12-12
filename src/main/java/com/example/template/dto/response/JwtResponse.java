package com.example.template.dto.response;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * JWT Login Response DTO
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Schema(description = "JWT Login Response")
public class JwtResponse {

    @Schema(description = "JWT Token", example = "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9...")
    private String token;

    @Schema(description = "Token Type", example = "Bearer")
    private String type = "Bearer";

    @Schema(description = "Username", example = "apiuser")
    private String username;

    public JwtResponse(String token, String username) {
        this.token = token;
        this.username = username;
    }

}
