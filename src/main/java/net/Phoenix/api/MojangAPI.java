package net.Phoenix.api;

import com.google.common.collect.Lists;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonParser;
import net.Phoenix.Utilities;
import net.Phoenix.api.objects.MojangUUID;
import net.Phoenix.api.objects.MojangUUIDList;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.concurrent.*;

import static java.util.concurrent.TimeUnit.SECONDS;

public class MojangAPI {

    public static MojangUUID getPlayerUUID(String playerName) throws IOException {
        String json = Utilities.queryAPI(MojangEndpoints.PLAYER.getUrl().replace("{username}", playerName));
        return MojangUUID.deserialize(json);
    }

    public static MojangUUIDList getPlayerUUIDs(List<String> playerNames) throws InterruptedException, ExecutionException {
        List<Callable<String>> callableTasks = new ArrayList<>();
        List<List<String>> players = Lists.partition(playerNames, 10);
        for (List<String> playerList : players) {
            JsonArray array = new JsonArray();
            for (String playerName : playerList) {
                array.add(playerName);
            }
            Callable<String> callableTask = () -> Utilities.postAPI(MojangEndpoints.PLAYERTOUUIDS.getUrl(), array.toString());
            callableTasks.add(callableTask);
        }
        ExecutorService executorService = Executors.newFixedThreadPool(100);
        List<Future<String>> futures = executorService.invokeAll(callableTasks);

        JsonArray array = new JsonArray();
        for(Future<String> stringFuture : futures){
            System.out.println(stringFuture);
            JsonArray tenArray = JsonParser.parseString(stringFuture.get()).getAsJsonArray();
            for(JsonElement e : tenArray.asList()){
                array.add(e.getAsJsonObject().toString());
            }
        }
        return MojangUUIDList.deserialize(Utilities.postAPI(MojangEndpoints.PLAYERTOUUIDS.getUrl(), array.toString()));
    }

    public enum MojangEndpoints {
        PLAYER("https://api.mojang.com/users/profiles/minecraft/{username}"),
        PLAYERTOUUIDS("https://api.mojang.com/profiles/minecraft");

        private final String url;

        MojangEndpoints(String url) {
            this.url = url;
        }

        public String getUrl() {
            return url;
        }
    }

}
