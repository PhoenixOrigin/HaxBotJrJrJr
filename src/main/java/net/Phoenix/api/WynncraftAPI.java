package net.Phoenix.api;

import net.Phoenix.Utilities;
import net.Phoenix.api.objects.Player;

import java.io.IOException;

public class WynncraftAPI {

    public Player getPlayerStats(String player) throws IOException {
        String url = WynncraftEndpoints.PLAYER.getUrl().replace("{PLAYER}", player);
        return Player.deserialize(Utilities.queryAPI(url));
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
    }
}
