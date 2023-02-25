package net.Phoenix.handlers;

import net.Phoenix.features.SignupFeature;
import net.Phoenix.features.commands.*;
import net.Phoenix.utilities.annotations.Event;
import net.dv8tion.jda.api.entities.emoji.Emoji;
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;

import javax.imageio.ImageIO;
import java.awt.*;
import java.io.File;

public class SlashCommandHandler {
    public static void handleSlashCommand(SlashCommandInteractionEvent event) {
        //event.deferReply(true).queue();
        switch (event.getName()) {
            case "sp" -> {
                // Checking if command enabled
                if (!ConfigHandler.getConfigBool("soul_point_command")) return;
                // Handling Command
                SoulPointCommand.handleEvent(event);
            }
            case "playtime" -> {
                // Checking if command enabled
                if (!ConfigHandler.getConfigBool("playtime_command")) return;
                // Handling Command
                PlaytimeCommand.handleEvent(event);
            }
            //case "botping" -> {
                // Checking if command enabled
                //if (!ConfigHandler.getConfigBool("ping_command")) return;
                // Handling Command
                //PingCommand.handleEvent(event);
            //}
            case "message" -> MessageCommand.handleEvent(event);
            case "signup" -> SignupFeature.handleCommand(event);
            case "allmessage" -> AllMessageCommand.handleEvent(event);
            case "help" -> HelpCommand.handleEvent(event);
            default -> {return;}
                // Handler for old unused commands
                    //event.getHook().editOriginal("Hmmmm, something has gone wrong. Please contact PhoenixOrigin#7083 or wait ~10minutes")
                            //.queue();
        }
    }

}
