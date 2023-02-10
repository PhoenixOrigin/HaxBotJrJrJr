package net.Phoenix.features.commands;

import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;

public class PingCommand {

    public static void handleEvent(SlashCommandInteractionEvent event) {
        event.getJDA().getRestPing().queue((time) -> {
            event.getHook().editOriginalFormat("Hi! The rest ping is: %dms and the gateway ping is: %dms",
                    time, event.getJDA().getGatewayPing())
                    .queue();
        });
    }
}
