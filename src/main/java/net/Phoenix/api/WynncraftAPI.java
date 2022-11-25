package net.Phoenix.api;

import net.Phoenix.api.objects.Player;

public class WynncraftAPI {

    public void getPlayerStats(String player){
        String url = WynncraftEndpoints.PLAYER.getUrl().replace("{PLAYER}", player);
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
