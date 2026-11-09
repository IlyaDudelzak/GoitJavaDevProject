package com.dudelzak.goit.java.devproject.shortlink;

import com.dudelzak.goit.java.devproject.shortlink.dto.CreateShortLinkRequest;
import com.dudelzak.goit.java.devproject.shortlink.dto.EditShortLinkRequest;
import com.dudelzak.goit.java.devproject.shortlink.dto.ShortLinkResponse;
import com.dudelzak.goit.java.devproject.security.User;
import com.dudelzak.goit.java.devproject.security.SecurityUtils;
import com.dudelzak.goit.java.devproject.shortlink.exception.*;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.List;
import java.util.Objects;

@Service
@RequiredArgsConstructor
public class ShortLinkService {

    private final ShortLinkRepository shortLinkRepository;
    private final SecurityUtils securityUtils;
    private final CodeGenerator codeGenerator;

    public ShortLinkResponse getShortLink(String shortCode) {
        return getShortLink(shortLinkRepository.findByShortCode(shortCode).orElseThrow(NoSuchLinkException::new));
    }

    public ShortLinkResponse getShortLink(ShortLink link) {
        return ShortLinkResponse.builder()
                .enabled(link.isEnabled())
                .originalUrl(link.getOriginalUrl())
                .shortCode(link.getShortCode())
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
                .map(link -> ShortLinkResponse.builder()
                        .enabled(link.isEnabled())
                        .originalUrl(link.getOriginalUrl())
                        .shortCode(link.getShortCode())
                        .createdAt(link.getCreatedAt())
                        .expiresAt(link.getExpiresAt())
                        .visits(link.getVisits())
                        .createdBy(link.getUser().getUsername())
                        .build())
                .toList();
    }

    public ShortLinkResponse createShortLink(CreateShortLinkRequest request) {
        String shortCode;
        if(request.getCustomCode() != null && !request.getCustomCode().isBlank())
            shortCode = request.getCustomCode();
        else
            shortCode = codeGenerator.generateCode();

        if(shortLinkRepository.existsByShortCode(shortCode)) throw new ShortLinkWithCodeAlreadyExistsException();
        ShortLink link =
                ShortLink.builder()
                        .enabled(true)
                        .createdAt(Instant.now())
                        .editedAt(Instant.now())
                        .expiresAt(request.getExpiresAt() != null ? request.getExpiresAt() : Instant.now().plus(30, ChronoUnit.DAYS))
                        .originalUrl(request.getOriginalUrl())
                        .shortCode(shortCode)
                        .visits(0L)
                        .user(securityUtils.getCurrentUser())
                        .build();
        if(request.getEnabled() != null) link.setEnabled(request.getEnabled());
        shortLinkRepository.save(link);
        return getShortLink(shortCode);
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
        return getShortLink(link);
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
