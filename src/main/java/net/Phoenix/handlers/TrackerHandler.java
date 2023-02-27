package net.Phoenix.handlers;

import net.Phoenix.api.AthenaAPI;
import net.Phoenix.trackers.Players;
import net.Phoenix.utilities.Utilities;

import java.util.*;

public class TrackerHandler {

    public static void queueTrackers(){
        Timer timer = new Timer();
        timer.scheduleAtFixedRate(
                new java.util.TimerTask() {
                    @Override
                    public void run() {
                        List<UUID> uuids = Utilities.getPlayersUUIDs(AthenaAPI.getAvailableServers().getOnlinePlayers());
                        Players.updatePlayerPlaytime(uuids);
                    }
                    },
                new Date(),
                300000
        );
    }


}
