package net.Phoenix.api;

import net.Phoenix.utilities.Titrator;

import java.time.Duration;
import java.util.concurrent.Semaphore;

public class PlayerRequestThread extends Thread {

    private static final int MAX_REQUESTS_PER_SECOND = 50;
    private static final int MAX_REQUESTS_PER_MINUTE = 180;
    private static final Semaphore semaphore = new Semaphore(MAX_REQUESTS_PER_SECOND);
    private static final Titrator titrator = new Titrator(MAX_REQUESTS_PER_MINUTE, Duration.ofMinutes(1));


    @Override
    public void run(){
        try {
            semaphore.acquire();
            titrator.consume();
        } catch (InterruptedException e) {
            // Handle the interruption
        } finally {
            semaphore.release();
            titrator.release();
        }
    }
}
