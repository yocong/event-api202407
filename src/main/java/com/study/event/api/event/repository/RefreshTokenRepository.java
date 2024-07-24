package com.study.event.api.event.repository;

import com.study.event.api.event.entity.EventUser;
import com.study.event.api.event.entity.RefreshToken;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface RefreshTokenRepository extends JpaRepository<RefreshToken, String> {
    Optional<RefreshToken> findByToken(String token);
    Optional<RefreshToken> findByEventUser(EventUser eventUser);
    void deleteByEventUser(EventUser eventUser);
}
