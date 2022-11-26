package net.Phoenix.api.objects;

public class PlayerLocation {
    private final boolean online;
    private final String server;

    public PlayerLocation(boolean online, String server) {
        this.online = online;
        this.server = server;
    }

    public boolean isOnline() {
        return online;
    }

    public String isServer() {
        return server;
    }
}
