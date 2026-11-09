package com.dudelzak.goit.java.devproject.security.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Schema(description = "User authentication credentials")
public class AuthRequest {
    @NotBlank
    @Schema(description = "Username", example = "John_Doe")
    private String username;

    @NotBlank
    @Schema(description = "User password", example = "Password123")
    private String password;

    public AuthRequest(RegisterRequest r) {
        this(r.getUsername(), r.getPassword());
    }
}

