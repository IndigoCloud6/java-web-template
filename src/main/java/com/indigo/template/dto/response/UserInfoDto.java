package com.indigo.template.dto.response;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

/**
 * User Info DTO
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Schema(description = "User Information")
public class UserInfoDto {

    @Schema(description = "User ID", example = "1")
    private Long id;

    @Schema(description = "Username", example = "apiuser")
    private String username;

    @Schema(description = "Email", example = "apiuser@example.com")
    private String email;

    @Schema(description = "Phone", example = "13800138000")
    private String phone;

    @Schema(description = "Nickname", example = "API User")
    private String nickname;

    @Schema(description = "Avatar URL", example = "https://example.com/avatar.jpg")
    private String avatar;

    @Schema(description = "User status (0:disabled, 1:active)", example = "1")
    private Integer status;

    @Schema(description = "User roles")
    private List<String> roles;
}
