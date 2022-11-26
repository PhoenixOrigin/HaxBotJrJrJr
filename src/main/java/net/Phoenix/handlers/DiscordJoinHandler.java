package net.Phoenix.handlers;

import net.Phoenix.features.GuildMemberJoinFeature;
import net.dv8tion.jda.api.events.guild.member.GuildMemberJoinEvent;

public class DiscordJoinHandler {

    public static void handleDiscordJoin(GuildMemberJoinEvent event) {
        // If not enabled return
        if (!ConfigHandler.getConfigBool("discord_welcome_image") || ConfigHandler.getConfigBool("disable_all")) return;
        // Handling event
        GuildMemberJoinFeature.handleEvent(event);
    }

}
