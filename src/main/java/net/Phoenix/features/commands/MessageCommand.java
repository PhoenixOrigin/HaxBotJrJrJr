package net.Phoenix.features.commands;

import net.Phoenix.utilities.paginators.embeds.MultiPagedEmbed;
import net.Phoenix.utilities.paginators.embeds.MultiPagedEmbedBuilder;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.entities.MessageEmbed;
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;

import java.util.ArrayList;
import java.util.List;

public class MessageCommand {

    public static void handleEvent(SlashCommandInteractionEvent event){
        EmbedBuilder builder = new EmbedBuilder();
        builder.setAuthor("phoenix");
        builder.setDescription("phoenix is hot (mltippage embed testing)");
        List<MessageEmbed> emeds = new ArrayList<>();
        emeds.add(builder.build());
        builder.setDescription("phoenix is so hot (mltippage embed testing)");
        emeds.add(builder.build());
        builder.setDescription("phoenix is so so so hot (mltippage embed testing)");
        emeds.add(builder.build());
        new MultiPagedEmbedBuilder()
            .setChannel(event.getChannel())
            .loadEmbeds(emeds)
            .deleteOnFinish(false)
            .createAndSend();
        if(!event.getMember().getId().equals("780889323162566697")) {
            event.getHook().editOriginal("What are you doing \uD83E\uDD14").queue();
            return;
        }
        event.getOption("channel").getAsChannel().asTextChannel().sendMessage(event.getOption("message").getAsString()).queue();
    }

}
