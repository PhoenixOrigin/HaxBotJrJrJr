package net.Phoenix.features.commands;

import net.Phoenix.handlers.ConfigHandler;
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;

public class FeatureCommand {

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
}
