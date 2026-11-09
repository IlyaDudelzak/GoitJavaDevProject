package com.dudelzak.goit.java.devproject.shortlink.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;

import java.time.Instant;

@Data
@Builder
@AllArgsConstructor
@Schema(description = "Short link details response")
public class ShortLinkResponse {
    @Schema(description = "Shortened code identifier", example = "abc123")
    private String shortCode;

    @Schema(description = "Original destination URL", example = "https://example.com")
    private String originalUrl;

    @Schema(description = "Total number of redirects/visits", example = "42")
    private Long visits;

    @Schema(description = "Creation timestamp (UTC)", example = "2026-11-08T15:00:00Z")
    private Instant createdAt;

    @Schema(description = "Last update timestamp (UTC)", example = "2026-11-08T15:30:00Z")
    private Instant editedAt;

    @Schema(description = "Expiration timestamp (UTC)", example = "2027-01-01T00:00:00Z")
    private Instant expiresAt;

    @Schema(description = "Whether the link is active and accessible", example = "true")
    private boolean enabled;

    @Schema(description = "Username of the user that created this link", example = "John_Doe")
    private String createdBy;
}

