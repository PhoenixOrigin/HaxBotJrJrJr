package net.Phoenix.api;

import net.Phoenix.api.objects.Player;
import net.Phoenix.utilities.ResourceRateLimit;

import java.io.IOException;
import java.util.concurrent.Callable;

public class PlayerRequest implements Callable<Player> {

    private final String player;
    private final ResourceRateLimit second;
    private final ResourceRateLimit minute;

    public PlayerRequest(String player, ResourceRateLimit second, ResourceRateLimit minute){
        this.player = player;
        this.second = second;
        this.minute = minute;
    }

    @Override
    public Player call() throws Exception{
        second.consume();
        minute.consume();

        WynncraftAPI api = new WynncraftAPI();
        try {
            return api.getPlayerStats(player);
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }
}
