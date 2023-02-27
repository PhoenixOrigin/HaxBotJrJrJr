package net.Phoenix.features.commands;

import net.Phoenix.utilities.annotations.BridgeCommand;
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;
import net.dv8tion.jda.api.interactions.commands.OptionType;

@BridgeCommand(name = "message",
        description = "Send a message using the bot",
        options = {
            @BridgeCommand.CommandOption(type = OptionType.STRING,
                    name = "message",
                    description = "The message to send",
                    required = true
            ),
            @BridgeCommand.CommandOption(type = OptionType.CHANNEL,
                    name = "channel",
                    description = "The channel to send the message in",
                    required = true
            )
        }
)
public class MessageCommand {

    @BridgeCommand.invoke
    public static void handleEvent(SlashCommandInteractionEvent event){
        if(!event.getMember().getId().equals("780889323162566697")) {
            event.getHook().editOriginal("What are you doing \uD83E\uDD14").queue();
            return;
        }
        event.getOption("channel").getAsChannel().asTextChannel().sendMessage(event.getOption("message").getAsString()).queue();
    }

}
