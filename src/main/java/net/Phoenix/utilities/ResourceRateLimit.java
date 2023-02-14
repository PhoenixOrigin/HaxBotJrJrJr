package net.Phoenix.utilities;

import java.time.Duration;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.*;
import java.util.concurrent.atomic.AtomicInteger;

public class ResourceRateLimit {

    private final int numDosesPerSecond = 50;
    private final int numDosesPerMinute = 180;
    private final AtomicInteger secondDosesReleased = new AtomicInteger(0);
    private final AtomicInteger minuteDosesReleased = new AtomicInteger(0);
    private final AtomicInteger remainingDosesSecond = new AtomicInteger(numDosesPerSecond);
    private final AtomicInteger remainingDosesMinute = new AtomicInteger(numDosesPerMinute);
    private final ScheduledExecutorService scheduler = Executors.newScheduledThreadPool(2);
    private final ScheduledFuture<?> minutes;
    private final ScheduledFuture<?> seconds;
    private long secondsUpdateMillis = 0;
    private long minutesUpdateMillis = 0;
    private final Map<Long, Long> consumedIds = new HashMap<>();

    public ResourceRateLimit() {
        seconds = scheduler.scheduleAtFixedRate(this::refillSeconds, 0, 1, TimeUnit.SECONDS);
        minutes = scheduler.scheduleAtFixedRate(this::refillMinutes, 0, 1, TimeUnit.MINUTES);
    }

    private void refillSeconds() {
        int released = secondDosesReleased.getAndSet(0);
        remainingDosesSecond.addAndGet(secondDosesReleased.get());
        secondsUpdateMillis = System.nanoTime();
    }

    private void refillMinutes() {
        int released = minuteDosesReleased.getAndSet(0);
        remainingDosesMinute.addAndGet(minuteDosesReleased.get());
        minutesUpdateMillis = System.nanoTime();
    }

    public long consume() throws InterruptedException {
        while (true) {
            int remainingSeconds = remainingDosesSecond.get();
            int remainingMinutes = remainingDosesMinute.get();

            if (remainingSeconds == 0 || remainingMinutes == 0) {
                long minutesDelay = minutes.getDelay(TimeUnit.SECONDS);
                long secondsDelay = seconds.getDelay(TimeUnit.SECONDS);

                if (minutesDelay < 0 && secondsDelay < 0) {
                    Thread.onSpinWait();
                } else if (minutesDelay > secondsDelay) {
                    Thread.sleep(Duration.ofSeconds(minutesDelay).toMillis());
                } else {
                    Thread.sleep(Duration.ofSeconds(secondsDelay).toMillis());
                }
            } else {
                remainingDosesSecond.decrementAndGet();
                remainingDosesMinute.decrementAndGet();

                long id = UUID.randomUUID().getMostSignificantBits() & Long.MAX_VALUE;
                consumedIds.put(id, System.currentTimeMillis());
                return id;
            }
        }
    }

    public void release(long id) {
        Long consumed = consumedIds.remove(id);
        if (consumed == null) {
            return;
        }
        if (!(consumed < secondsUpdateMillis)) {
            secondDosesReleased.incrementAndGet();
        } else if (!(consumed < minutesUpdateMillis)) {
            minuteDosesReleased.incrementAndGet();
        }
    }

    public void shutdown() {
        scheduler.shutdown();
    }

}
