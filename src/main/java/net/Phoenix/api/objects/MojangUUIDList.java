package net.Phoenix.api.objects;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public class MojangUUIDList {

    private final List<MojangPlayer> players;

    public MojangUUIDList(List<MojangPlayer> players) {
        this.players = players;
    }

    public List<MojangPlayer> getPlayers() {
        return players;
    }

    public static class MojangPlayer {
        private final String name;
        private final UUID uuid;

        public MojangPlayer(String name, UUID uuid) {
            this.name = name;
            this.uuid = uuid;
        }

        public String getName() {
            return name;
        }

        public UUID getUuid() {
            return uuid;
        }
    }

    public static MojangUUIDList deserialize(String json) {
        JsonArray object = JsonParser.parseString(json).getAsJsonArray();
        List<MojangPlayer> playerList = new ArrayList<>();
        for(JsonElement el : object.asList()){
            JsonObject player = el.getAsJsonObject();
            UUID uuid = UUID.fromString(player.get("id").getAsString().replaceFirst (
                    "(\\p{XDigit}{8})(\\p{XDigit}{4})(\\p{XDigit}{4})(\\p{XDigit}{4})(\\p{XDigit}+)",
                    "$1-$2-$3-$4-$5"
            ));
            MojangPlayer p = new MojangPlayer(player.get("name").getAsString(), uuid);
            playerList.add(p);
        }
        return new MojangUUIDList(playerList);
    }

}
