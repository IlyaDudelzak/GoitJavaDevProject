package com.dudelzak.goit.java.devproject.security.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
@Schema(description = "Authentication response containing JWT token")
public class AuthResponse {
    @Schema(description = "JWT bearer token", example = "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9...")
    private String token;

    @Schema(description = "Authenticated username", example = "John_Doe")
    private String name;

    @Schema(description = "Expiration timestamp of the token in milliseconds", example = "1731085200000")
    private Long expiresAt;
}

