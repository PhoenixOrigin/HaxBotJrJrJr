package net.Phoenix.handlers;

import net.Phoenix.trackers.Players;

import java.util.Timer;

public class TrackerHandler {

    public static void queueTrackers(){
        Timer timer = new Timer();
        timer.schedule(
                new java.util.TimerTask() {
                                   @Override
                                   public void run() {
                                       Players.updatePlayerPlaytime();
                                   }
                                   },
                300000
        );
    }

}
