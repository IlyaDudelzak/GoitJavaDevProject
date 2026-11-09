package com.dudelzak.goit.java.devproject.security;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Table;
import jakarta.persistence.Id;
import lombok.*;

import java.time.Instant;

@Entity
@Table(name = "users")
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Schema(description = "User account entity")
public class User {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Schema(description = "Unique user identifier", example = "1")
    private Long id;

    @Schema(description = "Username", example = "John_Doe")
    private String username;

    @Schema(description = "Hashed password")
    private String password;

    @Schema(description = "Account creation timestamp (UTC)", example = "2026-11-08T15:00:00Z")
    private Instant createdAt;
}