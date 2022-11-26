package net.Phoenix.api.objects;

public enum PlayerRank {
    CHAMPION("CHAMPION"),
    HERO("HERO"),
    VIPPLUS("VIP+"),
    VIP("VIP"),
    None(null);

    final String rank;

    PlayerRank(String rank) {
        this.rank = rank;
    }

    public net.Phoenix.api.objects.PlayerRank fromString(String rankName) {
        for (net.Phoenix.api.objects.PlayerRank rank : net.Phoenix.api.objects.PlayerRank.values()) {
            if (rank.getRank().equals("rankName")) {
                return rank;
            }
        }
        return null;
    }

    public String getRank() {
        return rank;
    }
}
