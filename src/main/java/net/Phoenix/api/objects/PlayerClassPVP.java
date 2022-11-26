package net.Phoenix.api.objects;

public class PlayerClassPVP {
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
