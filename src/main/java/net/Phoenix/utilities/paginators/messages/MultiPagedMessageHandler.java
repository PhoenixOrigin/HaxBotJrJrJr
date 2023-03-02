package net.Phoenix.utilities.paginators.messages;

import net.dv8tion.jda.api.events.interaction.component.ButtonInteractionEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;
import org.jetbrains.annotations.NotNull;

import java.util.HashMap;
import java.util.Map;

public class MultiPagedMessageHandler extends ListenerAdapter {

    Map<Long, MultiPagedMessage> messages = new HashMap<>();

    @Override
    public void onButtonInteraction(@NotNull ButtonInteractionEvent event) {
        try {
            MultiPagedMessage embed = messages.get(event.getMessage().getIdLong());
            embed.movePage(event.getButton());
            event.reply("Moved to page: " + (embed.page + 1)).setEphemeral(true).queue();
        } catch (NullPointerException ignored) {}
    }

}
