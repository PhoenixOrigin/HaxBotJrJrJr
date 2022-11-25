package net.Phoenix.events;

import net.Phoenix.handlers.ConfigHandler;
import net.Phoenix.handlers.DiscordJoinHandler;
import net.Phoenix.handlers.SlashCommandHandler;
import net.dv8tion.jda.api.events.GenericEvent;
import net.dv8tion.jda.api.events.guild.member.GuildMemberJoinEvent;
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;
import net.dv8tion.jda.api.hooks.EventListener;
import org.jetbrains.annotations.NotNull;

public class EventHandler implements EventListener {

    @Override
    public void onEvent(@NotNull GenericEvent event) {
        if (event instanceof GuildMemberJoinEvent) {
            DiscordJoinHandler.handleDiscordJoin((GuildMemberJoinEvent) event);
        }
        if (event instanceof SlashCommandInteractionEvent) {
            SlashCommandHandler.handleSlashCommand((SlashCommandInteractionEvent) event);
        }
    }

}
