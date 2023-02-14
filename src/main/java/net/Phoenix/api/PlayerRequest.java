package net.Phoenix.api;

import com.google.common.util.concurrent.RateLimiter;
import net.Phoenix.api.objects.Player;
import net.Phoenix.utilities.Titrator;

import java.io.IOException;
import java.util.concurrent.Callable;

public class PlayerRequest implements Callable<Player> {

    private final String player;
    private final Titrator second;
    private final Titrator minute;

    public PlayerRequest(String player, Titrator second, Titrator minute){
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
