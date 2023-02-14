package net.Phoenix.utilities;

import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;

public class ResourceRateLimit {

    private final int numDosesPerSecond;
    private final int numDosesPerMinute;

    private final AtomicInteger remainingDosesSecond;
    private final AtomicInteger remainingDosesMinute;



    public ResourceRateLimit(int numDosesPerSecond, int seconds, int numDosesPerMinute, int minutes) {
        this.numDosesPerSecond = numDosesPerSecond;
        this.remainingDosesSecond = new AtomicInteger(this.numDosesPerSecond);
        this.numDosesPerMinute = numDosesPerMinute;
        this.remainingDosesMinute = new AtomicInteger(this.numDosesPerMinute);
        ScheduledExecutorService scheduler = Executors.newScheduledThreadPool(2);
        scheduler.scheduleAtFixedRate(this::refillSeconds, 0, seconds, TimeUnit.SECONDS);
        scheduler.scheduleAtFixedRate(this::refillSeconds, 0, minutes, TimeUnit.MINUTES);
    }

    public void refillSeconds(){
        remainingDosesSecond.set(numDosesPerSecond);
    }

    public void refillMinutes(){
        remainingDosesMinute.set(numDosesPerMinute);
    }

    public void consume() {
        while (remainingDosesSecond.get() == 0 || remainingDosesMinute.get() == 0) {
        }
        remainingDosesSecond.decrementAndGet();
        remainingDosesMinute.decrementAndGet();
    }

}