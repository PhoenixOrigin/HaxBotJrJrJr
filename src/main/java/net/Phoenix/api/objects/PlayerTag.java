package net.Phoenix.api.objects;

public class PlayerTag {
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

}
