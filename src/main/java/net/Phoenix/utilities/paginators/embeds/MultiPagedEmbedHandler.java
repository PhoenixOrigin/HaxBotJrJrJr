package net.Phoenix.utilities.paginators.embeds;

import net.dv8tion.jda.api.events.interaction.component.ButtonInteractionEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;
import org.jetbrains.annotations.NotNull;

import java.util.HashMap;
import java.util.Map;

public class MultiPagedEmbedHandler extends ListenerAdapter {

    Map<Long, MultiPagedEmbed> messages = new HashMap<>();

    @Override
    public void onButtonInteraction(@NotNull ButtonInteractionEvent event) {
        MultiPagedEmbed embed = messages.get(event.getMessage().getIdLong());
        embed.movePage(event.getButton());
        event.reply("Moved to page: " + (embed.page + 1)).setEphemeral(true).queue();
    }

}
