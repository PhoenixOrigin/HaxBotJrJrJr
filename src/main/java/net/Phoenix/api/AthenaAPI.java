package net.Phoenix.api;

import net.Phoenix.api.objects.AthenaServerList;

import static net.Phoenix.Utilities.queryAPI;

public class AthenaAPI {

    public static AthenaServerList getAvailableServers() {
        // Querying api and returning a serverList deserialized json
        return AthenaServerList.deserialize(queryAPI(AthenaEndpoint.SERVER_LIST.getUrl()));
    }

    public enum AthenaEndpoint {
        MAP_LOCATIONS("https://athena.wynntils.com/cache/get/mapLocations"),
        LEADERBOARD("https://athena.wynntils.com/cache/get/leaderboard"),
        ITEM_LIST("https://athena.wynntils.com/cache/get/itemList"),
        INGREDIENT_LIST("https://athena.wynntils.com/cache/get/ingredientList"),
        GATHERING_SPOTS("https://athena.wynntils.com/cache/get/gatheringSpots"),
        SERVER_LIST("https://athena.wynntils.com/cache/get/serverList"),
        TERRITORY_LIST("https://athena.wynntils.com/cache/get/territoryList"),
        HASHES("https://athena.wynntils.com/cache/getHashes");


        final String url;

        AthenaEndpoint(String url) {
            this.url = url;
        }

        public String getUrl() {
            return url;
        }
    }

}
