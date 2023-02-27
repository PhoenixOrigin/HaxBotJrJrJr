package net.Phoenix.features.commands;

import com.google.common.annotations.Beta;
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;
import net.dv8tion.jda.api.interactions.commands.build.SlashCommandData;
import net.dv8tion.jda.api.interactions.commands.build.SubcommandGroupData;

@Beta
public class PointsCommand {

    public static void handleCommand(SlashCommandInteractionEvent event){
        event.getMember().canInteract(event.getGuild().getRoleById("villager what are you doing"));
    }

}
