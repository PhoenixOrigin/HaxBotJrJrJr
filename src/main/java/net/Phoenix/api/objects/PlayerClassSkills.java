package net.Phoenix.api.objects;

public class PlayerClassSkills {
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
