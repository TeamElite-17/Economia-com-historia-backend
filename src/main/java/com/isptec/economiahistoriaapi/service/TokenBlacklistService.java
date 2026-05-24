package com.isptec.economiahistoriaapi.service;

import com.isptec.economiahistoriaapi.config.JwtUtil;
import lombok.RequiredArgsConstructor;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import java.util.Date;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Blacklist de tokens JWT invalidados (logout).
 * Os tokens são removidos automaticamente após expirarem para evitar fuga de memória.
 */
@Service
@RequiredArgsConstructor
public class TokenBlacklistService {

    private final JwtUtil jwtUtil;

    // token -> data de expiração
    private final Map<String, Date> blacklist = new ConcurrentHashMap<>();

    /** Adiciona um token à blacklist (logout). */
    public void invalidate(String token) {
        Date expiry = jwtUtil.extractExpiration(token);
        if (expiry != null) {
            blacklist.put(token, expiry);
        }
    }

    /** Verifica se o token está na blacklist. */
    public boolean isBlacklisted(String token) {
        return blacklist.containsKey(token);
    }

    /** Remove tokens já expirados da blacklist (corre a cada hora). */
    @Scheduled(fixedRate = 3_600_000)
    public void purgeExpired() {
        Date now = new Date();
        blacklist.entrySet().removeIf(entry -> entry.getValue().before(now));
    }
}
