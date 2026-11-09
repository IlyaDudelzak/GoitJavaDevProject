package com.dudelzak.goit.java.devproject.shortlink;

import com.dudelzak.goit.java.devproject.shortlink.dto.CreateShortLinkRequest;
import com.dudelzak.goit.java.devproject.shortlink.dto.EditShortLinkRequest;
import com.dudelzak.goit.java.devproject.shortlink.dto.ShortLinkResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.io.IOException;
import java.util.List;

@Tag(name = "Short Links", description = "Endpoints for managing and resolving short links")
@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/link/")
class ShortLinkController {

    private final ShortLinkService shortLinkService;

    @Operation(summary = "Create short link", description = "Creates a new shortened link with optional custom code and expiration", security = @SecurityRequirement(name = "BearerAuth"))
    @PostMapping("create")
    public ShortLinkResponse createShortLink(@Valid @RequestBody CreateShortLinkRequest request) {
        return shortLinkService.createShortLink(request);
    }

    @Operation(summary = "Edit short link", description = "Updates an existing short link's original URL, status, or expiration", security = @SecurityRequirement(name = "BearerAuth"))
    @PostMapping("edit")
    public ShortLinkResponse editShortLink(@Valid @RequestBody EditShortLinkRequest request) {
        return shortLinkService.editShortLink(request);
    }

    @Operation(summary = "Get all links", description = "Retrieves all short links created by the authenticated user", security = @SecurityRequirement(name = "BearerAuth"))
    @GetMapping("all")
    public List<ShortLinkResponse> getAllLinksByUser() {
        return shortLinkService.getAllLinksByUser(false);
    }

    @Operation(summary = "Get active links", description = "Retrieves only active and non-expired short links created by the authenticated user", security = @SecurityRequirement(name = "BearerAuth"))
    @GetMapping("active")
    public List<ShortLinkResponse> getAllActiveLinksByUser() {
        return shortLinkService.getAllLinksByUser(true);
    }

    @Operation(summary = "Get link details", description = "Retrieves details and statistics for a specific short link by its code", security = @SecurityRequirement(name = "BearerAuth"))
    @GetMapping("details/{shortCode}")
    public ShortLinkResponse getShortLink(
            @Parameter(description = "Short code of the link", example = "abc123")
            @PathVariable String shortCode) {
        return shortLinkService.getShortLinkWithAuthCheck(shortCode);
    }

    @Operation(summary = "Delete short link", description = "Deletes a short link owned by the authenticated user", security = @SecurityRequirement(name = "BearerAuth"))
    @DeleteMapping("delete/{shortCode}")
    public ResponseEntity<Void> deleteShortLink(
            @Parameter(description = "Short code of the link to delete", example = "abc123")
            @PathVariable String shortCode) {
        shortLinkService.deleteShortLink(shortCode);
        return ResponseEntity.noContent().build();
    }

    @Operation(summary = "Redirect to original URL", description = "Increments visit counter and redirects to the original URL")
    @GetMapping("go/{shortCode}")
    public void redirectToshortLink(
            @Parameter(description = "Short code to resolve and redirect to", example = "abc123")
            @PathVariable String shortCode,
            HttpServletResponse response) throws IOException {
        String originalUrl = shortLinkService.getOriginalUrl(shortCode);
        response.sendRedirect(originalUrl);
    }
}


