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

    public static class PlayerData {

        private final String username;
        private final UUID uuid;
        private final String rank;
        private final PlayerMeta playerMeta;
        private final List<PlayerClass> playerClasses;

        public String getUsername() {
            return username;
        }

        public UUID getUuid() {
            return uuid;
        }

        public String getRank() {
            return rank;
        }

        public PlayerMeta getPlayerMeta() {
            return playerMeta;
        }

        public List<PlayerClass> getPlayerClasses() {
            return playerClasses;
        }

        public PlayerData(String username, UUID uuid, String rank, PlayerMeta playerMeta, List<PlayerClass> playerClasses) {
            this.username = username;
            this.uuid = uuid;
            this.rank = rank;
            this.playerMeta = playerMeta;
            this.playerClasses = playerClasses;
        }


        public static class PlayerClass {
            private final PlayerClassType playerClassType;
            private final int totalLevel;
            private final PlayerClassPVP playerClassPVP;
            private final long blocksWalked;
            private final int logins;

            public PlayerClassType getPlayerClassType() {
                return playerClassType;
            }

            public int getTotalLevel() {
                return totalLevel;
            }

            public PlayerClassPVP getPlayerClassPVP() {
                return playerClassPVP;
            }

            public long getBlocksWalked() {
                return blocksWalked;
            }

            public int getLogins() {
                return logins;
            }

            public int getDeaths() {
                return deaths;
            }

            public int getPlaytime() {
                return playtime;
            }

            public PlayerClassSkills getPlayerClassSkills() {
                return playerClassSkills;
            }

            public PlayerClassProfessions getProfessions() {
                return professions;
            }

            public int getDiscoveries() {
                return discoveries;
            }

            public int getEventsWon() {
                return eventsWon;
            }

            public boolean isPreEconomyUpdate() {
                return preEconomyUpdate;
            }

            public PlayerClass(PlayerClassType playerClassType, int totalLevel, PlayerClassPVP playerClassPVP, long blocksWalked, int logins, int deaths, int playtime, PlayerClassSkills playerClassSkills, PlayerClassProfessions professions, int discoveries, int eventsWon, boolean preEconomyUpdate) {
                this.playerClassType = playerClassType;
                this.totalLevel = totalLevel;
                this.playerClassPVP = playerClassPVP;
                this.blocksWalked = blocksWalked;
                this.logins = logins;
                this.deaths = deaths;
                this.playtime = playtime;
                this.playerClassSkills = playerClassSkills;
                this.professions = professions;
                this.discoveries = discoveries;
                this.eventsWon = eventsWon;
                this.preEconomyUpdate = preEconomyUpdate;
            }

            private final int deaths;
            private final int playtime;
            private final PlayerClassSkills playerClassSkills;
            private final PlayerClassProfessions professions;
            private final int discoveries;
            private final int eventsWon;
            private final boolean preEconomyUpdate;

            public static class PlayerClassProfessions{

                private final List<PlayerClassProfession> professions;

                public PlayerClassProfessions(List<PlayerClassProfession> professions) {
                    this.professions = professions;
                }

                public static class PlayerClassProfession{
                    private final String name;

                    public String getName() {
                        return name;
                    }

                    public int getLevel() {
                        return level;
                    }

                    public float getXp() {
                        return xp;
                    }

                    public PlayerClassProfession(String name, int level, float xp) {
                        this.name = name;
                        this.level = level;
                        this.xp = xp;
                    }

                    private final int level;
                    private final float xp;
                }

            }
            public static class PlayerClassSkills{
                private final int strength;
                private final int dexterity;
                private final int intelligence;
                private final int defense;
                private final int agility;

                public PlayerClassSkills(int strength, int dexterity, int intelligence, int defense, int agility) {
                    this.strength = strength;
                    this.dexterity = dexterity;
                    this.intelligence = intelligence;
                    this.defense = defense;
                    this.agility = agility;
                }

                public int getStrength() {
                    return strength;
                }

                public int getDexterity() {
                    return dexterity;
                }

                public int getIntelligence() {
                    return intelligence;
                }

                public int getDefense() {
                    return defense;
                }

                public int getAgility() {
                    return agility;
                }
            }
            public static class PlayerClassPVP{
                private final int kills;
                private final int deaths;

                public PlayerClassPVP(int kills, int deaths) {
                    this.kills = kills;
                    this.deaths = deaths;
                }

                public int getKills() {
                    return kills;
                }

                public int getDeaths() {
                    return deaths;
                }
            }
            public enum PlayerClassType {
                DARKWIZARD("DARKWIZARD"),
                KNIGHT("KNIGHT"),
                ARCHER("ARCHER"),
                SKYSEER("SKYSEER"),
                NINJA("NINJA"),
                SHAMAN("SHAMAN"),
                MAGE("MAGE"),
                WARRIOR("WARRIOR"),
                HUNTER("HUNTER"),
                ASSASSIN("ASSASIN");

                final String name;

                PlayerClassType(String name){
                    this.name = name;
                }

                public PlayerClassType fromString(String className){
                    for(PlayerClassType type : PlayerClassType.values()){
                        if(type.name.equals(className)){
                            return type;
                        }
                    }
                    return null;
                }
            }
        }

        public static class PlayerMeta {
            private final String firstJoin;
            private final String lastSeen;
            private final PlayerLocation playerLocation;
            private final long playtime;
            private final PlayerTag playerTag;
            private final boolean veteran;

            public PlayerMeta(String firstJoin, String lastSeen, PlayerLocation playerLocation, long playtime, PlayerTag playerTag, boolean veteran) {
                this.firstJoin = firstJoin;
                this.lastSeen = lastSeen;
                this.playerLocation = playerLocation;
                this.playtime = playtime;
                this.playerTag = playerTag;
                this.veteran = veteran;
            }

            public String getLastSeen() {
                return lastSeen;
            }

            public String getFirstJoin() {
                return firstJoin;
            }

            public PlayerLocation getPlayerLocation() {
                return playerLocation;
            }

            public boolean isVeteran() {
                return veteran;
            }

            public PlayerTag getPlayerTag() {
                return playerTag;
            }

            public long getPlaytime() {
                return playtime;
            }

            public static class PlayerTag {
                private final boolean display;
                private final PlayerRank playerRank;

                public PlayerTag(boolean display, PlayerRank playerRank) {
                    this.display = display;
                    this.playerRank = playerRank;
                }

                public PlayerRank getPlayerRank() {
                    return playerRank;
                }

                public boolean isDisplay() {
                    return display;
                }

                public enum PlayerRank {
                    CHAMPION("CHAMPION"),
                    HERO("HERO"),
                    VIPPLUS("VIP+"),
                    VIP("VIP"),
                    None(null);

                    final String rank;

                    PlayerRank(String rank){
                        this.rank = rank;
                    }

                    public PlayerRank fromString(String rankName){
                        for(PlayerRank rank : PlayerRank.values()){
                            if(rank.getRank().equals("rankName")){
                                return rank;
                            }
                        }
                        return null;
                    }

                    public String getRank() {
                        return rank;
                    }
                }
            }

            public static class PlayerLocation {
                private final boolean online;
                private final boolean server;

                public PlayerLocation(boolean online, boolean server) {
                    this.online = online;
                    this.server = server;
                }

                public boolean isOnline() {
                    return online;
                }

                public boolean isServer() {
                    return server;
                }
            }

        }

    }

    public static void deserialize(String json){
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
        PlayerData.PlayerMeta.PlayerLocation playerLocation = new PlayerData.PlayerMeta.PlayerLocation(location.get("online").getAsBoolean(), location.get("server").getAsString());
        long playtime = (long) (meta.get("playtime").getAsLong() * 4.7);
        JsonObject playerTag = meta.get("tag").getAsJsonObject();
        PlayerData.PlayerMeta.PlayerTag tag = new PlayerData.PlayerMeta.PlayerTag(playerTag.get("display").getAsBoolean(), PlayerData.PlayerMeta.PlayerTag.PlayerRank.valueOf(playerTag.get("value")));
        PlayerData.PlayerMeta playerMeta = new PlayerData.PlayerMeta(firstJoin, lastSeen, playerLocation, playtime, tag, meta.get("veteran").getAsBoolean());
        List<PlayerData.PlayerClass> playerClasses = new ArrayList<>();
        for(Map.Entry<String, JsonElement> classInLoop : playerData.get("characters").getAsJsonObject().entrySet()){
            JsonObject playerClass = classInLoop.getValue().getAsJsonObject();
            PlayerData.PlayerClass.PlayerClassType type = PlayerData.PlayerClass.PlayerClassType.valueOf(playerClass.get("type").getAsString()0;
            int totalLevel = playerClass.get("level").getAsInt();
            PlayerData.PlayerClass.PlayerClassPVP pvp = new PlayerData.PlayerClass.PlayerClassPVP(playerClass.get("pvp").getAsJsonObject().get("kills").getAsInt(), playerClass.get("pvp").getAsJsonObject().get("deaths").getAsInt());
            long blocksWalked = playerClass.get("blocksWalked").getAsLong();
            int logins = playerClass.get("logins").getAsInt();
            int deaths = playerClass.get("deaths").getAsInt();
            int classPlaytime = (int) (playerClass.get("playtime").getAsInt() * 4.7);
            this.playerClassSkills = playerClassSkills;
            this.professions = professions;
            this.discoveries = discoveries;
            this.eventsWon = eventsWon;
            this.preEconomyUpdate = preEconomyUpdate;
            PlayerData.PlayerClass theclass = new PlayerData.PlayerClass()
        }
        PlayerData data = new PlayerData(username, uuid, rank, playerMeta, playerClasses);
    }
}
