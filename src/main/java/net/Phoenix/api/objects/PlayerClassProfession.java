package net.Phoenix.api.objects;

public class PlayerClassProfession {
    private final String name;
    private final int level;
    private final float xp;

    public PlayerClassProfession(String name, int level, float xp) {
        this.name = name;
        this.level = level;
        this.xp = xp;
    }

    public String getName() {
        return name;
    }

    public int getLevel() {
        return level;
    }

    public float getXp() {
        return xp;
    }

}
