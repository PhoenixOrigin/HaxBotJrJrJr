package net.Phoenix.utilities;

import java.time.Duration;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;

public class Titrator {

    private final int numDosesPerPeriod;
    private final AtomicInteger remainingDoses;


    public Titrator(int numDosesPerPeriod, Duration period) {
        this.numDosesPerPeriod = numDosesPerPeriod;
        this.remainingDoses = new AtomicInteger(numDosesPerPeriod);
        ScheduledExecutorService scheduler = Executors.newSingleThreadScheduledExecutor();
        scheduler.scheduleAtFixedRate(this::refill, 0, period.toMillis(), TimeUnit.MILLISECONDS);
    }

    public void refill(){
        remainingDoses.set(numDosesPerPeriod);
    }

    public void consume() {
        while (remainingDoses.get() == 0) {
            Thread.onSpinWait();
        }
        remainingDoses.decrementAndGet();
    }

}