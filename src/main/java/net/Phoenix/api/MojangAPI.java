package net.Phoenix.api;

import net.Phoenix.Utilities;
import net.Phoenix.api.objects.MojangUUID;

public class MojangAPI {

    public static MojangUUID getPlayerUUID(String playerName){
        String json = Utilities.queryAPI(MojangEndpoints.PLAYER.getUrl().replace("{username}", playerName));
        return MojangUUID.deserialize(json);
    }

    public enum MojangEndpoints {
        PLAYER("https://api.mojang.com/users/profiles/minecraft/{username}");

        private final String url;

        MojangEndpoints(String url) {
            this.url = url;
        }

        public String getUrl() {
            return url;
        }
    }

}
