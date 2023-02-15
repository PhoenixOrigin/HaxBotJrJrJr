package net.Phoenix.api;

import jdk.jshell.execution.Util;
import net.Phoenix.Main;
import net.Phoenix.utilities.Utilities;
import net.Phoenix.api.objects.Player;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;

public class WynncraftAPI {


    public Player getPlayerStats(String player) throws IOException {
        WynncraftEndpoints.PLAYER.consumeLimit();
        String url = WynncraftEndpoints.PLAYER.getUrl().replace("{PLAYER}", player);
        String json = Utilities.queryAPI(url);
        WynncraftEndpoints.PLAYER.releaseLimit();
        return Player.deserialize(json);
    }

    public static List<Player> getPlayersStats(List<String> playerNames) {
        int numberOfRequests = playerNames.size();
        ExecutorService executor = Executors.newFixedThreadPool(numberOfRequests);
        List<Future<Player>> futures = new ArrayList<>();
        for (String playerName : playerNames) {
            PlayerRequest requestThread = new PlayerRequest(playerName);
            Future<Player> future = executor.submit(requestThread);
            futures.add(future);
        }
        List<Player> results = new ArrayList<>();
        for (Future<Player> future : futures) {
            try {
                results.add(future.get());
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        executor.shutdown();
        return results;
    }

    public enum WynncraftEndpoints {
        PLAYER("https://api.wynncraft.com/v2/player/{PLAYER}/stats");


        private final String url;

        WynncraftEndpoints(String url) {
            this.url = url;
        }

        public String getUrl() {
            return url;
        }

        public void consumeLimit(){
            Main.playerRateLimit.consume();
            Main.connectionRateLimit.consume();
        }

        public void releaseLimit(){
            Main.playerRateLimit.release();
            Main.connectionRateLimit.release();
        }

    }
}
