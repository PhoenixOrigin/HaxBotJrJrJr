package net.Phoenix.api.objects;

import java.util.List;

public class PlayerClassProfessions {

    private final List<PlayerClassProfession> professions;

    public PlayerClassProfessions(List<PlayerClassProfession> professions) {
        this.professions = professions;
    }

    public List<PlayerClassProfession> getProfessions() {
        return professions;
    }
}
