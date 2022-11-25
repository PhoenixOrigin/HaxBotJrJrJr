package net.Phoenix.api.objects;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class ServerList {

    private final List<Server> servers;

    public ServerList(List<Server> servers) {
        // Initializer
        this.servers = servers;
    }

    public List<Server> getServers() {
        // Simple getter
        return servers;
    }

    public static ServerList deserialize(String response) {
        // Parse JSON  into this
        JsonObject jsonObject = JsonParser.parseString(response).getAsJsonObject();
        // Creating list to input into later
        List<Server> serverList = new ArrayList<>();
        // Looping through servers
        for (Map.Entry<String, JsonElement> object : jsonObject.get("servers").getAsJsonObject().entrySet()) {
            // Empty player list
            List<String> players = new ArrayList<>();
            // Looping through players
            for (JsonElement string : object.getValue().getAsJsonObject().get("players").getAsJsonArray()) {
                // Populating list
                players.add(string.getAsString());
            }
            // Adding a new server to list
            serverList.add(new Server(object.getValue().getAsJsonObject().get("firstSeen").getAsLong(), players, object.getKey()));
        }
        // Creating new class of this and returning
        return new ServerList(serverList);
    }

    public static class Server {

        private final long firstSeen;
        private final List<String> players;

        private final String server;

        public Server(long firstSeen, List<String> players, String server) {
            // Initializer
            this.firstSeen = firstSeen;
            this.players = players;
            this.server = server;
        }

        public long getFirstSeen() {
            // Simple getter
            return firstSeen;
        }

        public long getUptimeMinutes() {
            // Simple getter
            long difference = System.currentTimeMillis() - firstSeen;
            return (difference / 1000) / 60;
        }

        public String getServer() {
            // Simple getter
            return server;
        }

        public List<String> getPlayers() {
            // Simple getter
            return players;
        }
    }


}
