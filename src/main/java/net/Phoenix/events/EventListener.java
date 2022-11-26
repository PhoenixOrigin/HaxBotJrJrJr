package net.Phoenix.events;

import net.Phoenix.handlers.DiscordJoinHandler;
import net.Phoenix.handlers.SlashCommandHandler;
import net.dv8tion.jda.api.events.GenericEvent;
import net.dv8tion.jda.api.events.guild.member.GuildMemberJoinEvent;
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;
import net.dv8tion.jda.api.events.interaction.command.UserContextInteractionEvent;
import org.jetbrains.annotations.NotNull;

public class EventListener implements net.dv8tion.jda.api.hooks.EventListener {

    @Override
    public void onEvent(@NotNull GenericEvent event) {
        if (event instanceof GuildMemberJoinEvent) {
            DiscordJoinHandler.handleDiscordJoin((GuildMemberJoinEvent) event);
        }
        if (event instanceof SlashCommandInteractionEvent) {
            SlashCommandHandler.handleSlashCommand((SlashCommandInteractionEvent) event);
        }
        if (event instanceof UserContextInteractionEvent)
    }

}
