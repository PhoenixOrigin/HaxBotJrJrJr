package net.Phoenix.api.objects;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.UUID;

public class Player {

    private final String kind;
    private final int code;
    private final long timestamp;
    private final String version;
    private final PlayerData playerData;

    public Player(String kind, int code, long timestamp, String version, PlayerData playerData) {
        this.kind = kind;
        this.code = code;
        this.timestamp = timestamp;
        this.version = version;
        this.playerData = playerData;
    }

    public static Player deserialize(String json) {
        JsonObject object = JsonParser.parseString(json).getAsJsonObject();
        String kind = object.get("kind").getAsString();
        int code = object.get("code").getAsInt();
        long timestamp = object.get("timestamp").getAsLong();
        String version = object.get("version").getAsString();
        JsonObject playerData = object.get("data").getAsJsonArray().get(0).getAsJsonObject();
        String username = playerData.get("username").getAsString();
        UUID uuid = UUID.fromString(playerData.get("uuid").getAsString());
        String rank = playerData.get("rank").getAsString();
        JsonObject meta = playerData.get("meta").getAsJsonObject();
        String firstJoin = meta.get("firstJoin").getAsString();
        String lastSeen = meta.get("lastSeen").getAsString();
        JsonObject location = meta.get("location").getAsJsonObject();
        PlayerLocation playerLocation = new PlayerLocation(location.get("online").getAsBoolean(), location.get("server").getAsString());
        long playtime = (long) (meta.get("playtime").getAsLong() * 4.7);
        JsonObject playerTag = meta.get("tag").getAsJsonObject();
        PlayerTag tag = new PlayerTag(playerTag.get("display").getAsBoolean(), PlayerRank.valueOf(playerTag.get("value").getAsString()));
        PlayerMeta playerMeta = new PlayerMeta(firstJoin, lastSeen, playerLocation, playtime, tag, meta.get("veteran").getAsBoolean());
        List<PlayerClass> playerClasses = new ArrayList<>();
        for (Map.Entry<String, JsonElement> classInLoop : playerData.get("characters").getAsJsonObject().entrySet()) {
            JsonObject playerClass = classInLoop.getValue().getAsJsonObject();
            PlayerClassType type = PlayerClassType.valueOf(playerClass.get("type").getAsString());
            int totalLevel = playerClass.get("level").getAsInt();
            PlayerClassPVP pvp = new PlayerClassPVP(playerClass.get("pvp").getAsJsonObject().get("kills").getAsInt(), playerClass.get("pvp").getAsJsonObject().get("deaths").getAsInt());
            long blocksWalked = playerClass.get("blocksWalked").getAsLong();
            int logins = playerClass.get("logins").getAsInt();
            int deaths = playerClass.get("deaths").getAsInt();
            int classPlaytime = (int) (playerClass.get("playtime").getAsInt() * 4.7);
            JsonObject skills = playerClass.get("skills").getAsJsonObject();
            int strength = skills.get("strength").getAsInt();
            int dexterity = skills.get("dexterity").getAsInt();
            int intelligence = skills.get("intelligence").getAsInt();
            int defense = skills.get("defense").getAsInt();
            int agility = skills.get("agility").getAsInt();
            PlayerClassSkills classSkills = new PlayerClassSkills(strength, dexterity, intelligence, defense, agility);
            List<PlayerClassProfession> professions = new ArrayList<>();
            for (Map.Entry<String, JsonElement> prof : playerClass.get("professions").getAsJsonObject().entrySet()) {
                PlayerClassProfession profession = new PlayerClassProfession(prof.getKey(), prof.getValue().getAsJsonObject().get("level").getAsInt(), prof.getValue().getAsJsonObject().get("xp").getAsFloat());
                professions.add(profession);
            }
            PlayerClassProfessions profess = new PlayerClassProfessions(professions);
            int discoveries = playerClass.get("discoveries").getAsInt();
            int eventsWon = playerClass.get("eventsWon").getAsInt();
            boolean preEconomyUpdate = playerClass.get("preEconomyUpdate").getAsBoolean();
            PlayerClass theclass = new PlayerClass(type, totalLevel, pvp, blocksWalked, logins, deaths, classPlaytime, classSkills, profess, discoveries, eventsWon, preEconomyUpdate);
            playerClasses.add(theclass);
        }
        PlayerData data = new PlayerData(username, uuid, rank, playerMeta, playerClasses);
        return new Player(kind, code, timestamp, version, data);
    }

    public PlayerData getPlayerData() {
        return playerData;
    }

    public String getVersion() {
        return version;
    }

    public long getTimestamp() {
        return timestamp;
    }

    public int getCode() {
        return code;
    }

    public String getKind() {
        return kind;
    }
}
