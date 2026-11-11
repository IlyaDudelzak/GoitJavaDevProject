package com.dudelzak.goit.java.devproject.shortlink;

import com.dudelzak.goit.java.devproject.security.User;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.persistence.*;
import jakarta.validation.constraints.Future;
import jakarta.validation.constraints.Size;
import lombok.*;

import org.hibernate.annotations.DynamicUpdate;

import java.time.Instant;

@Entity
@Table(name = "shortlinks")
@DynamicUpdate
@Setter
@Getter
@Builder(toBuilder = true)
@NoArgsConstructor
@AllArgsConstructor
@Schema(description = "Shortened link entity")
public class ShortLink {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Schema(description = "Unique link identifier", example = "1")
    private Long id;

    @Size(min = 6, max = 8)
    @Schema(description = "Shortened code of the link", example = "abc123")
    private String shortCode;

    @Schema(description = "Original destination URL", example = "https://example.com")
    private String originalUrl;

    @Schema(description = "Total number of redirects on this link", example = "42")
    private Long visits;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    @Schema(description = "Owner of the short link")
    private User user;

    @Schema(description = "Creation timestamp (UTC)", example = "2026-11-08T15:00:00Z")
    private Instant createdAt;

    @Schema(description = "Last modification timestamp (UTC)", example = "2026-11-08T15:30:00Z")
    private Instant editedAt;

    @Schema(description = "Expiration timestamp (UTC)", example = "2027-01-01T00:00:00Z")
    private Instant expiresAt;

    @Schema(description = "Indicates whether the link is enabled", example = "true")
    private boolean enabled;
}
