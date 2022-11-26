package net.Phoenix.handlers;

import net.Phoenix.features.commands.FeatureCommand;
import net.dv8tion.jda.api.events.interaction.command.CommandAutoCompleteInteractionEvent;

public class CommandAutoCompleteHandler {

    public static void handleEvent(CommandAutoCompleteInteractionEvent event) {
        if (event.getName().equals("feature")) FeatureCommand.handleAutoComplete(event);
    }

}
