package net.Phoenix.api.objects;

public class PlayerClass {
    private final PlayerClassType playerClassType;
    private final int totalLevel;
    private final PlayerClassPVP playerClassPVP;
    private final long blocksWalked;
    private final int logins;
    private final int deaths;
    private final int playtime;
    private final PlayerClassSkills playerClassSkills;
    private final PlayerClassProfessions professions;
    private final int discoveries;
    private final int eventsWon;
    private final boolean preEconomyUpdate;

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

}
