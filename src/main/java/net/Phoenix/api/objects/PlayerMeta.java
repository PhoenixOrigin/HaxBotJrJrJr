package net.Phoenix.api.objects;

public class PlayerMeta {
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

}
