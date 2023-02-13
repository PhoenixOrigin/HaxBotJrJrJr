package net.Phoenix.api;

import com.google.common.util.concurrent.RateLimiter;
import net.Phoenix.utilities.Titrator;

import java.time.Duration;
import java.util.concurrent.Semaphore;
import java.util.concurrent.TimeUnit;

public class PlayerRequestThread extends Thread {

    private static final int MAX_REQUESTS_PER_SECOND = 50;
    private static final int MAX_REQUESTS_PER_MINUTE = 180;
    private static final RateLimiter second = RateLimiter.create(MAX_REQUESTS_PER_SECOND);
    private static final RateLimiter minute = RateLimiter.create(MAX_REQUESTS_PER_MINUTE, 0, TimeUnit.MINUTES);

    public void run(String player){
        second.acquire();
        minute.acquire();

    }
}
