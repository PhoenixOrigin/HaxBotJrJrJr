package net.Phoenix.handlers;

import net.Phoenix.api.AthenaAPI;
import net.Phoenix.trackers.Players;
import net.Phoenix.utilities.Utilities;

import java.util.*;

public class TrackerHandler {

    public static List<UUID> uuids = Utilities.getPlayersUUIDs(AthenaAPI.getAvailableServers().getOnlinePlayers());

    public static void queueTrackers(){
        Timer timer = new Timer();
        Timer timer2 = new Timer();
        Timer timer3 = new Timer();
        timer.scheduleAtFixedRate(
                new java.util.TimerTask() {
                    @Override
                    public void run() {
                        Players.updatePlayerPlaytime(TrackerHandler.uuids);
                    }
                    },
                new Date(),
                300000
        );
        timer2.scheduleAtFixedRate(
                new java.util.TimerTask() {
                    @Override
                    public void run() {
                        Players.updatePlayerRaids(TrackerHandler.uuids);
                    }
                },
                new Date(),
                3600000
        );
        timer3.scheduleAtFixedRate(new TimerTask() {
            @Override
            public void run() {
                TrackerHandler.uuids = Utilities.getPlayersUUIDs(AthenaAPI.getAvailableServers().getOnlinePlayers());
            }
        }, new Date(), 300000);

    }


}
