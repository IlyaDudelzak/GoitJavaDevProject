package com.dudelzak.goit.java.devproject.shortlink;

import com.dudelzak.goit.java.devproject.security.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;
import java.util.List;
import java.util.Optional;

public interface ShortLinkRepository extends JpaRepository<ShortLink, Long> {

    Optional<ShortLink> findByShortCode(String shortCode);
    List<ShortLink> findAllByUser(User user);
    List<ShortLink> findAllByUserAndEnabledTrueAndExpiresAtAfter(User user, Instant now);

    boolean existsByShortCode(String shortCode);

    @Modifying
    @Transactional
    @Query("UPDATE ShortLink s SET s.visits = COALESCE(s.visits, 0) + 1 WHERE s.shortCode = :shortCode")
    void incrementVisits(@Param("shortCode") String shortCode);
}
