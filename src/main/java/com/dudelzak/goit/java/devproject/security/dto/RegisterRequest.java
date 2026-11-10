package com.dudelzak.goit.java.devproject.security.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Schema(description = "User registration payload")
public class RegisterRequest {
    @NotBlank
    @Size(min = 3, max=16)
    @Schema(description = "Username (3 to 16 characters)", example = "John_Doe")
    private String username;

    @NotBlank
    @Pattern(regexp = "^(?=.*[0-9])(?=.*[a-z])(?=.*[A-Z]).{8,128}$", message = "Password must be at least 8 characters long, and contain at leat one number, lowercase and uppercase letter")
    @Schema(description = "Password (min 8 chars, at least one digit, uppercase and lowercase letter)", example = "Password123")
    private String password;
}

