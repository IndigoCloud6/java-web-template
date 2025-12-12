package com.indigo.template.dto.request;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import lombok.Data;

/**
 * Login Request DTO
 */
@Data
@Schema(description = "Login Request")
public class LoginRequest {

    @NotBlank(message = "Username cannot be blank")
    @Schema(description = "Username", example = "apiuser")
    private String username;

    @NotBlank(message = "Password cannot be blank")
    @Schema(description = "Password", example = "apipass")
    private String password;

}
