package ai_client.retry;

public class RateLimiter {
    private final long minIntervalMs;
    private long lastRequestTime = 0;

    public RateLimiter(long minIntervalMs) {
        this.minIntervalMs = minIntervalMs;
    }

    public void waitIfNeeded() {
        long now = System.currentTimeMillis();
        long elapsedTime = now - lastRequestTime;
        if (elapsedTime < minIntervalMs) {
            sleep(minIntervalMs - elapsedTime);
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
