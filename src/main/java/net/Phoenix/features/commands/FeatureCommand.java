package net.Phoenix.features.commands;

import net.Phoenix.handlers.ConfigHandler;
import net.dv8tion.jda.api.events.interaction.command.CommandAutoCompleteInteractionEvent;
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;
import net.dv8tion.jda.api.interactions.commands.Command;

import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class FeatureCommand {

    /*
    ! Features activation
!Disable all features
disable_all = false
!Enables soul point command
soul_point_command = true
!Enables config modifier command
config_modify_command = true
!Enables welcome image
discord_welcome_image = true
!All database things
database = true
     */

    private static final String[] features = new String[]{"disable_all", "soul_point_command", "feature_enable_command", "discord_welcome_image", "databse"};


    public static void handleEvent(SlashCommandInteractionEvent event) {
        // Getting which feature to toggle
        String feature = event.getOption("feature").getAsString();
        // Checking whether to enable or disable
        boolean enabled = event.getOption("enabled").getAsBoolean();
        // Toggling
        ConfigHandler.toggleFeature(feature, enabled);
        // Updating message
        event.getHook().editOriginal(String.format("The feature '%s' has been toggled to %s", feature, enabled ? "enabled" : "disabled")).queue();
    }

    public static void handleAutoComplete(CommandAutoCompleteInteractionEvent event) {
        if (event.getFocusedOption().getName().equals("feature")) {
            List<Command.Choice> options = Stream.of(features)
                    .map(word -> new Command.Choice(word, word))
                    .collect(Collectors.toList());
            event.replyChoices(options).queue();
        }
    }

}
