package ai_client.retry;

import java.util.function.Supplier;

public class RetryHandler {
    private static final int MAX_RETRIES = 3;

    public <T> T execute(Supplier<T> action, T fallbackValue) {
        int attempts = 0;
        long delay = 1000;

        while (attempts < MAX_RETRIES) {
            try {
                return action.get();
            } catch (Exception e) {
                attempts++;
                if (attempts >= MAX_RETRIES) {
                    return fallbackValue;
                }
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
