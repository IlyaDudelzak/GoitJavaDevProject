package com.dudelzak.goit.java.devproject.shortlink;

import com.dudelzak.goit.java.devproject.shortlink.dto.CreateShortLinkRequest;
import com.dudelzak.goit.java.devproject.shortlink.dto.EditShortLinkRequest;
import com.dudelzak.goit.java.devproject.shortlink.dto.ShortLinkResponse;
import com.dudelzak.goit.java.devproject.security.User;
import com.dudelzak.goit.java.devproject.security.SecurityUtils;
import com.dudelzak.goit.java.devproject.shortlink.exception.*;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.List;
import java.util.Objects;

@Service
public class ShortLinkService {
    private final String baseUrl;
    private final ShortLinkRepository shortLinkRepository;
    private final SecurityUtils securityUtils;
    private final CodeGenerator codeGenerator;

    public ShortLinkService(
            @Value("${app.base-url:${APP_BASE_URL:http://localhost:8080/api/v1/link/go/}}") String baseUrl,
            ShortLinkRepository shortLinkRepository,
            SecurityUtils securityUtils,
            CodeGenerator codeGenerator
    ) {
        if (baseUrl == null || baseUrl.isBlank()) {
            this.baseUrl = "http://localhost:8080/api/v1/link/go/";
        } else {
            this.baseUrl = baseUrl.endsWith("/") ? baseUrl : baseUrl + "/";
        }
        this.shortLinkRepository = shortLinkRepository;
        this.securityUtils = securityUtils;
        this.codeGenerator = codeGenerator;
    }

    public ShortLinkResponse getShortLinkWithAuthCheck(String shortCode) {
        ShortLink link =  shortLinkRepository.findByShortCode(shortCode).orElseThrow(NoSuchLinkException::new);

        Long currentUserId = securityUtils.getCurrentUserId();
        if (!link.getUser().getId().equals(currentUserId)) {
            throw new UserDoesNotOwnShortLinkException();
        }

        return getShortLink(link);
    }

    public ShortLinkResponse getShortLink(String shortCode) {
        return getShortLink(shortLinkRepository.findByShortCode(shortCode).orElseThrow(NoSuchLinkException::new));
    }

    public ShortLinkResponse getShortLink(ShortLink link) {
        String fullUrl = baseUrl + link.getShortCode();
        return ShortLinkResponse.builder()
                .enabled(link.isEnabled())
                .originalUrl(link.getOriginalUrl())
                .shortCode(link.getShortCode())
                .shortUrl(fullUrl)
                .shortLink(fullUrl)
                .createdAt(link.getCreatedAt())
                .editedAt(link.getEditedAt())
                .expiresAt(link.getExpiresAt())
                .visits(link.getVisits())
                .createdBy(link.getUser().getUsername())
                .build();
    }

    public List<ShortLinkResponse> getAllLinksByUser(boolean active) {
        User user = securityUtils.getCurrentUser();

        return
                (
                    active
                            ?
                                shortLinkRepository.findAllByUserAndEnabledTrueAndExpiresAtAfter(user, Instant.now())
                            :
                                shortLinkRepository.findAllByUser(user)
                )
                .stream()
                .map(this::getShortLink)
                .toList();
    }

    public ShortLinkResponse createShortLink(CreateShortLinkRequest request) {
        boolean isCustom = request.getCustomCode() != null && !request.getCustomCode().isBlank();
        int maxAttempts = isCustom ? 1 : 10;

        for (int attempt = 0; attempt < maxAttempts; attempt++) {
            String shortCode = isCustom ? request.getCustomCode() : codeGenerator.generateCode();
            if (shortLinkRepository.existsByShortCode(shortCode)) {
                if (isCustom) {
                    throw new ShortLinkWithCodeAlreadyExistsException();
                }
                continue;
            }

            ShortLink link = ShortLink.builder()
                    .enabled(request.getEnabled() == null || request.getEnabled())
                    .createdAt(Instant.now())
                    .editedAt(Instant.now())
                    .expiresAt(request.getExpiresAt() != null ? request.getExpiresAt() : Instant.now().plus(30, ChronoUnit.DAYS))
                    .originalUrl(request.getOriginalUrl())
                    .shortCode(shortCode)
                    .visits(0L)
                    .user(securityUtils.getCurrentUser())
                    .build();

            try {
                shortLinkRepository.saveAndFlush(link);
                return getShortLink(link);
            } catch (DataIntegrityViolationException e) {
                if (isCustom) {
                    throw new ShortLinkWithCodeAlreadyExistsException();
                }
                // Auto-generated code collision race condition: retry with a new code
            }
        }
        throw new ShortLinkWithCodeAlreadyExistsException();
    }

    @Transactional
    public ShortLinkResponse editShortLink(EditShortLinkRequest request) {
        ShortLink link = shortLinkRepository.findByShortCode(request.getShortCode())
                .orElseThrow(NoSuchLinkException::new);

        Long currentUserId = securityUtils.getCurrentUserId();
        if (!link.getUser().getId().equals(currentUserId)) {
            throw new UserDoesNotOwnShortLinkException();
        }

        boolean isUpdated = false;

        if (request.getOriginalUrl() != null && !request.getOriginalUrl().equals(link.getOriginalUrl())) {
            link.setOriginalUrl(request.getOriginalUrl());
            isUpdated = true;
        }
        if (request.getEnabled() != null && !request.getEnabled().equals(link.isEnabled())) {
            link.setEnabled(request.getEnabled());
            isUpdated = true;
        }
        if (request.getExpiresAt() != null && !request.getExpiresAt().equals(link.getExpiresAt())) {
            link.setExpiresAt(request.getExpiresAt());
            isUpdated = true;
        }
        if (!isUpdated) {
            return getShortLink(link);
        }
        link.setEditedAt(Instant.now());
        shortLinkRepository.save(link);
        return getShortLink(link.getShortCode());
    }

    public String getOriginalUrl(String shortCode) {
        ShortLink link = shortLinkRepository.findByShortCode(shortCode).orElseThrow(NoSuchLinkException::new);
        if(link.getExpiresAt().isBefore(Instant.now())) throw new LinkExpiredException();
        if(!link.isEnabled()) throw new LinkDisabledException();
        shortLinkRepository.incrementVisits(shortCode);
        return link.getOriginalUrl();
    }

    public void deleteShortLink(String shortCode) {
        Long currentUserId = securityUtils.getCurrentUserId();

        ShortLink link = shortLinkRepository.findByShortCode(shortCode)
                .orElseThrow(NoSuchLinkException::new);

        if (!Objects.equals(link.getUser().getId(), currentUserId)) {
            throw new UserDoesNotOwnShortLinkException();
        }

        shortLinkRepository.delete(link);
    }
}
