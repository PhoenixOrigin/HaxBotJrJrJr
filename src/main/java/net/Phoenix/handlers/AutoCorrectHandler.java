package net.Phoenix.handlers;

import net.Phoenix.features.SignupFeature;
import net.dv8tion.jda.api.events.interaction.command.CommandAutoCompleteInteractionEvent;

public class AutoCorrectHandler {

    public static void handleAutocompleteEvent(CommandAutoCompleteInteractionEvent event) {
        switch (event.getName()) {
            case "ping" -> SignupFeature.handleAutoComplete(event);
            default -> {
                return;
            }
        }
    }

}
