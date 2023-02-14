package net.Phoenix.utilities;

import java.time.Duration;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.*;
import java.util.concurrent.atomic.AtomicInteger;

public class ResourceRateLimit {

    private final AtomicInteger secondDosesReleased = new AtomicInteger(0);
    private final AtomicInteger minuteDosesReleased = new AtomicInteger(0);
    private final AtomicInteger remainingDosesSecond;
    private final AtomicInteger remainingDosesMinute;
    private final ScheduledExecutorService scheduler = Executors.newScheduledThreadPool(2);
    private final ScheduledFuture<?> minutes;
    private final ScheduledFuture<?> seconds;

    public ResourceRateLimit(int numDoesesPerSecond, int numDosesPerMinute) {
        remainingDosesSecond = new AtomicInteger(numDoesesPerSecond);
        remainingDosesMinute = new AtomicInteger(numDosesPerMinute);
        seconds = scheduler.scheduleAtFixedRate(this::refillSeconds, 0, 1, TimeUnit.SECONDS);
        minutes = scheduler.scheduleAtFixedRate(this::refillMinutes, 0, 1, TimeUnit.MINUTES);
    }

    private void refillSeconds() {
        int released = secondDosesReleased.getAndSet(0);
        remainingDosesSecond.addAndGet(released);
    }

    private void refillMinutes() {
        int released = minuteDosesReleased.getAndSet(0);
        remainingDosesMinute.addAndGet(released);
    }

    public void consume() throws InterruptedException {
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
            }
        }
    }

    public void release() {
        secondDosesReleased.incrementAndGet();
        minuteDosesReleased.incrementAndGet();
    }

    public void shutdown() {
        scheduler.shutdown();
    }

}
