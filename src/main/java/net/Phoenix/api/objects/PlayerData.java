package net.Phoenix.api.objects;

import java.util.List;
import java.util.UUID;

public class PlayerData {

    private final String username;
    private final UUID uuid;
    private final String rank;
    private final PlayerMeta playerMeta;
    private final List<PlayerClass> playerClasses;

    public PlayerData(String username, UUID uuid, String rank, PlayerMeta playerMeta, List<PlayerClass> playerClasses) {
        this.username = username;
        this.uuid = uuid;
        this.rank = rank;
        this.playerMeta = playerMeta;
        this.playerClasses = playerClasses;
    }

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

}
