package net.Phoenix.api.objects;

import com.google.gson.JsonObject;
import com.google.gson.JsonParser;

import java.util.UUID;

public class MojangUUID {

    private final String name;
    private final UUID uuid;

    public MojangUUID(String name, UUID uuid) {
        this.name = name;
        this.uuid = uuid;
    }

    public String getName() {
        return name;
    }

    public UUID getUuid() {
        return uuid;
    }

    public static MojangUUID deserialize(String apiResponse){
        JsonObject json = JsonParser.parseString(apiResponse).getAsJsonObject();
        String name = json.get("name").getAsString();
        UUID uuid = UUID.fromString(
                json.get("id").getAsString().replaceFirst (
                        "(\\p{XDigit}{8})(\\p{XDigit}{4})(\\p{XDigit}{4})(\\p{XDigit}{4})(\\p{XDigit}+)",
                        "$1-$2-$3-$4-$5"
                ));
        return new MojangUUID(name, uuid);
    }

}
