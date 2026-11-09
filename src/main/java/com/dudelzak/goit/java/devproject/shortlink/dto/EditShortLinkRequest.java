package com.dudelzak.goit.java.devproject.shortlink.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.Future;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.Instant;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
@Schema(description = "Request payload for updating an existing short link")
public class EditShortLinkRequest {
    @Schema(description = "Short code of the link to edit", example = "custom1")
    @NotNull
    private String shortCode;

    @Pattern(
            regexp = "^(https?://)(www\\.)?([a-zA-Z0-9-]+\\.)+[a-zA-Z]{2,}((:[0-9]+)?(/\\S*)?)?$",
            message = "Original URL must be a valid link"
    )
    @Schema(description = "Updated original destination URL", example = "https://example.com/updated")
    private String originalUrl;

    @Schema(description = "Updated active status of the link", example = "true")
    private Boolean enabled;

    @Future(message = "expiresAt must be in the future")
    @Schema(description = "Updated expiration timestamp (UTC)", example = "2027-01-01T00:00:00Z")
    private Instant expiresAt;
}

