package net.Phoenix.api;

import com.google.common.util.concurrent.RateLimiter;
import net.Phoenix.api.objects.Player;

import java.io.IOException;
import java.util.concurrent.Callable;

public class PlayerRequest implements Callable<Player> {

    private String player;
    private RateLimiter second;
    private RateLimiter minute;

    public PlayerRequest(String player, RateLimiter second, RateLimiter minute){
        this.player = player;
        this.second = second;
        this.minute = minute;
    }

    @Override
    public Player call() throws Exception{
        second.acquire();
        minute.acquire();

        WynncraftAPI api = new WynncraftAPI();
        try {
            return api.getPlayerStats(player);
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }
}
