package net.Phoenix.api.objects;

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

    PlayerClassType(String name) {
        this.name = name;
    }

    public net.Phoenix.api.objects.PlayerClassType fromString(String className) {
        for (net.Phoenix.api.objects.PlayerClassType type : PlayerClassType.values()) {
            if (type.name.equals(className)) {
                return type;
            }
        }
        return null;
    }
}
