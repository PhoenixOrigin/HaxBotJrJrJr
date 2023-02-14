package net.Phoenix.api;

import net.Phoenix.Main;
import net.Phoenix.api.objects.Player;
import net.Phoenix.utilities.ResourceRateLimit;

import java.io.IOException;
import java.util.concurrent.Callable;

public class PlayerRequest implements Callable<Player> {

    private final String player;

    public PlayerRequest(String player){
        this.player = player;
    }

    @Override
    public Player call() throws Exception{
        Main.playerRateLimit.consume();

        WynncraftAPI api = new WynncraftAPI();
        try {
            return api.getPlayerStats(player);
        } catch (IOException e) {
            e.printStackTrace();
        }

        Main.playerRateLimit.release();

        return null;
    }
}
