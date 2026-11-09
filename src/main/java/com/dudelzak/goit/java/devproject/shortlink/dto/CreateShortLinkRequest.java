package com.dudelzak.goit.java.devproject.shortlink.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.Future;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.Instant;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder(toBuilder = true)
@Schema(description = "Request payload for creating a new short link")
public class CreateShortLinkRequest {
    @Pattern(
            regexp = "^(https?://)(www\\.)?([a-zA-Z0-9-]+\\.)+[a-zA-Z]{2,}((:[0-9]+)?(/\\S*)?)?$",
            message = "Original URL must be a valid link"
    )
    @Size(max = 255, message = "Url too long")
    @Schema(description = "Original destination URL", example = "https://example.com")
    private String originalUrl;

    @Pattern(
            regexp = "^$|^[a-zA-Z0-9]{6,8}$",
            message = "Custom short code must be between 6 and 8 alphanumeric characters"
    )
    @Schema(description = "Optional custom short code (6-8 alphanumeric characters)", example = "custom1")
    private String customCode;

    @Schema(description = "Initial active status of the link", example = "true")
    private Boolean enabled;

    @Future
    @Schema(description = "Expiration timestamp (UTC) when the link becomes inactive", example = "2027-01-01T00:00:00Z")
    private Instant expiresAt;
}

