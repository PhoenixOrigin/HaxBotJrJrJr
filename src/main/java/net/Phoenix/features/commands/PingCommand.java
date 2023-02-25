package net.Phoenix.features.commands;

import net.Phoenix.utilities.annotations.BridgeCommand;
import net.dv8tion.jda.api.interactions.commands.SlashCommandInteraction;

@BridgeCommand(name = "ping",
        description = "Get the bot's ping"
)
public class PingCommand {
    @BridgeCommand.invoke
    public static void invoke(SlashCommandInteraction interaction) {
        interaction.deferReply().setEphemeral(true).queue();
        interaction.getJDA().getRestPing().queue((time) -> {
            interaction.getHook().editOriginalFormat("Hi! The rest ping is: %dms and the gateway ping is: %dms",
                            time, interaction.getJDA().getGatewayPing())
                    .queue();
        });
    }

    
}
