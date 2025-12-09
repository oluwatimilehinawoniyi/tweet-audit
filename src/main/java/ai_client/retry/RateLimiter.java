package ai_client.retry;

public class RateLimiter {
    private static final long MIN_INTERVAL_MS = 4000;
    private long lastRequestTime = 0;

    public void waitIfNeeded() {
        long now = System.currentTimeMillis();
        long elapsedTime = now - lastRequestTime;
        if (elapsedTime < MIN_INTERVAL_MS) {
            sleep(MIN_INTERVAL_MS - elapsedTime);
        }
        lastRequestTime = System.currentTimeMillis();
    }

    public void sleep(long millis) {
        try {
            Thread.sleep(millis);
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            throw new RuntimeException("Interrupted", e);
        }
    }
}
