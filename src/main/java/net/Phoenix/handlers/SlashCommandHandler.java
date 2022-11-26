package net.Phoenix.handlers;

import net.Phoenix.features.SoulPointCommandFeature;
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;

public class SlashCommandHandler {

    public static void handleSlashCommand(SlashCommandInteractionEvent event){
        event.deferReply(true).queue();
        if ("sp".equals(event.getName())) {// Checking if command enabled
            if (!ConfigHandler.getConfigBool("soul_point_command")) return;
            // Handling Command
            SoulPointCommandFeature.handleEvent(event);
            // Break switch
        } else {// Handler for old unused commands
            event.reply("Hmmmm, something has gone wrong. Please contact PhoenixOrigin#7083 or wait ~10minutes")
                    .setEphemeral(true)
                    .queue();
        }
    }

}
