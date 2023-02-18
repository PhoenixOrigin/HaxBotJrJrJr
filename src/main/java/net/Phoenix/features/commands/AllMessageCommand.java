package net.Phoenix.features.commands;

import net.dv8tion.jda.api.entities.channel.concrete.TextChannel;
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;

public class AllMessageCommand {

    public static void handleEvent(SlashCommandInteractionEvent event){
        if(!event.getMember().getId().equals("780889323162566697")) {
            event.getHook().editOriginal("What are you doing \uD83E\uDD14").queue();
            return;
        }
        for (TextChannel channel : event.getGuild().getTextChannels()) {
            try {
                channel.sendMessage(event.getOption("message").getAsString()).queue();
            } catch (Exception ignored) {
            }
        }
    }

}
