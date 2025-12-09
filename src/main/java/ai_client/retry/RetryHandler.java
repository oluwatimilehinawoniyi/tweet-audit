package ai_client.retry;

import lombok.extern.slf4j.Slf4j;

import java.util.function.Supplier;

@Slf4j
public class RetryHandler {
    private final int maxRetries;
    private final long initialDelayMs;

    public RetryHandler(int maxRetries, long initialDelayMs) {
        this.maxRetries = maxRetries;
        this.initialDelayMs = initialDelayMs;
    }

    public <T> T execute(Supplier<T> action, T fallbackValue) {
        int attempts = 0;
        long delay = initialDelayMs;

        while (attempts < maxRetries) {
            try {
                return action.get();
            } catch (Exception e) {
                attempts++;
                log.warn("Retry attempt {} failed: {}", attempts,
                        e.getMessage());
                if (attempts >= maxRetries) {
                    log.error("Failed after max retries ({} attempts) " +
                                    "exceeded: {}",
                            maxRetries, e.getMessage());
                    return fallbackValue;
                }

                log.info("Retrying in {}ms", delay);
                sleep(delay);
                delay *= 2;
            }
        }
        return fallbackValue;
    }

    private void sleep(long ms) {
        try {
            Thread.sleep(ms);
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            throw new RuntimeException("Interrupted", e);
        }
    }
}
