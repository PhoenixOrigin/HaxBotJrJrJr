package net.Phoenix.utilities;
import com.google.common.util.concurrent.RateLimiter;
import java.time.Duration;

public class Titrator {

    private final int numDosesPerPeriod;
    private final RateLimiter rateLimiter;
    private long numDosesAvailable;
    private transient final Object doseLock;

    public Titrator(int numDosesPerPeriod, Duration period) {
        this.numDosesPerPeriod = numDosesPerPeriod;
        double numSeconds = period.getSeconds() + period.getNano() / 1000000000d;
        rateLimiter = RateLimiter.create(1 / numSeconds);
        numDosesAvailable = 0L;
        doseLock = new Object();
    }

    /**
     * Consumes a dose from this titrator, blocking until a dose is available.
     */
    public void consume() {
        synchronized (doseLock) {
            if (numDosesAvailable == 0) { // then refill
                rateLimiter.acquire();
                numDosesAvailable += numDosesPerPeriod;
            }
            numDosesAvailable--;
        }
    }

}
