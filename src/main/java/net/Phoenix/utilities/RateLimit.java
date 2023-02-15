package net.Phoenix.utilities;

import java.time.Duration;
import java.util.concurrent.*;
import java.util.concurrent.atomic.AtomicInteger;

public class RateLimit {

    private final AtomicInteger dosesReleased = new AtomicInteger(0);
    private final AtomicInteger remainingDoses;
    private final ScheduledExecutorService scheduler = Executors.newScheduledThreadPool(1);
    private final ScheduledFuture<?> doseRefill;

    public RateLimit(int numDoses, int time, TimeUnit unit) {
        remainingDoses = new AtomicInteger(numDoses);
        doseRefill = scheduler.scheduleAtFixedRate(this::refill, 0, time, unit);
    }

    private void refill() {
        int released = dosesReleased.getAndSet(0);
        remainingDoses.addAndGet(released);
    }

    public void consume() {
        while (true) {
            int remainingMinutes = remainingDoses.get();

            if (remainingMinutes == 0) {
                long doseDelay = doseRefill.getDelay(TimeUnit.SECONDS);

                if (doseDelay < 0) {
                    Thread.onSpinWait();
                } else {
                    try {
                        Thread.sleep(Duration.ofSeconds(doseDelay).toMillis());
                    } catch (InterruptedException e) {
                        throw new RuntimeException(e);
                    }
                }
            } else {
                remainingDoses.decrementAndGet();
            }
        }
    }

    public void release() {
        dosesReleased.incrementAndGet();
    }

    public void shutdown() {
        scheduler.shutdown();
    }

}
