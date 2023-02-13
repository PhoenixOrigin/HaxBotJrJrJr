package net.Phoenix.handlers;

import net.Phoenix.features.commands.FeatureCommand;
import net.Phoenix.features.commands.PingCommand;
import net.Phoenix.features.commands.PlaytimeCommand;
import net.Phoenix.features.commands.SoulPointCommand;
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;

public class SlashCommandHandler {

    public static void handleSlashCommand(SlashCommandInteractionEvent event) {
        event.deferReply(true).queue();
        switch (event.getName()) {
            case "sp" -> {
                // Checking if command enabled
                if (!ConfigHandler.getConfigBool("soul_point_command")) return;
                // Handling Command
                SoulPointCommand.handleEvent(event);
            }
            // Break switch
            case "feature" -> {
                // Checking if command enabled
                if (!ConfigHandler.getConfigBool("feature_enable_command")) return;
                // Handling Command
                FeatureCommand.handleEvent(event);
            }
            case "playtime" -> {
                // Checking if command enabled
                if (!ConfigHandler.getConfigBool("playtime_command")) return;
                // Handling Command
                PlaytimeCommand.handleEvent(event);
            }
            case "botping" -> {
                // Checking if command enabled
                if (!ConfigHandler.getConfigBool("ping_command")) return;
                // Handling Command
                PingCommand.handleEvent(event);
            }
            default ->
                // Handler for old unused commands
                    event.getHook().editOriginal("Hmmmm, something has gone wrong. Please contact PhoenixOrigin#7083 or wait ~10minutes")
                            .queue();
        }
    }

}
