package net.Phoenix.handlers;

import net.Phoenix.trackers.Players;

import java.io.ObjectInputFilter;
import java.util.Date;
import java.util.Timer;

public class TrackerHandler {

    public static void queueTrackers(){
        Timer timer = new Timer();
        if(ConfigHandler.getConfigBool("playtime_tracker")) {
            timer.scheduleAtFixedRate(
                    new java.util.TimerTask() {
                        @Override
                        public void run() {
                            Players.updatePlayerPlaytime();
                        }
                    },
                    new Date(),
                    300000
            );
        }
    }

}
