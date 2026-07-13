package br.com.advec.connect.auth;

import java.time.Duration;
import java.time.Instant;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import org.springframework.stereotype.Service;

/** Bloqueio simples de forca bruta em memoria: 5 falhas -> 5 minutos de espera. */
@Service
public class LoginAttemptService {

    private static final int MAX_ATTEMPTS = 5;
    private static final Duration BLOCK_DURATION = Duration.ofMinutes(5);

    private record Attempts(int count, Instant lastFailure) {}

    private final Map<String, Attempts> attempts = new ConcurrentHashMap<>();

    public boolean isBlocked(String key) {
        Attempts current = attempts.get(key);
        if (current == null || current.count() < MAX_ATTEMPTS) {
            return false;
        }
        if (current.lastFailure().plus(BLOCK_DURATION).isBefore(Instant.now())) {
            attempts.remove(key);
            return false;
        }
        return true;
    }

    public void recordFailure(String key) {
        attempts.merge(key, new Attempts(1, Instant.now()),
            (old, ignored) -> new Attempts(old.count() + 1, Instant.now()));
    }

    public void recordSuccess(String key) {
        attempts.remove(key);
    }
}
