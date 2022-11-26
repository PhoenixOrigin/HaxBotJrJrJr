package net.Phoenix.trackers;

import net.Phoenix.handlers.ConfigHandler;

public class Guild {


    public static void queueTrackers() {

    }

    public static void trackUsers() {
        if (!ConfigHandler.getConfigBool("guild_rank")) {
        }
    }


}
