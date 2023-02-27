package net.Phoenix.features.commands;

import net.Phoenix.Main;
import net.Phoenix.utilities.annotations.BridgeCommand;
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;
import net.dv8tion.jda.api.interactions.commands.Command;

import java.util.List;

@BridgeCommand(name = "help",
        description = "The bot's help message"
)
public class HelpCommand {

    @BridgeCommand.invoke
    public static void handleEvent(SlashCommandInteractionEvent event) {
        event.deferReply(true).queue();

        List<Command> commands = Main.jda.retrieveCommands().complete();
        event.getHook().editOriginal(getHelpMessage(commands)).queue();
    }

    private static String getHelpMessage(List<Command> commands) {
        StringBuilder sb = new StringBuilder();
        sb.append("Available Commands:\n");

        for (Command command : commands) {
            sb.append("- **").append(command.getName()).append("** - ").append(command.getDescription()).append("\n");

            // Append subcommands
            if (!command.getSubcommands().isEmpty()) {
                sb.append(getSubcommandHelpMessage(command.getSubcommands(), 1));
            }

            // Append options
            for (Command.Option option : command.getOptions()) {
                sb.append(">     - ").append(option.getName()).append(" - ").append(option.getDescription()).append("\n");
            }

            sb.append("\n");
        }

        return sb.toString();
    }

    private static String getSubcommandHelpMessage(List<Command.Subcommand> subcommands, int depth) {
        StringBuilder sb = new StringBuilder();

        for (Command.Subcommand subcommand : subcommands) {
            // Add spacing for subcommands
            for (int i = 0; i < depth; i++) {
                sb.append(">    ");
            }

            // Append subcommand name and description
            sb.append("- *").append(subcommand.getName()).append("* - ").append(subcommand.getDescription()).append("\n");

            // Append options
            for (Command.Option option : subcommand.getOptions()) {
                sb.append(">");
                for (int i = 0; i < depth + 1; i++) {
                    sb.append("        ");
                }
                sb.append(option.getName()).append(" - ").append(option.getDescription()).append("\n");
            }
        }

        return sb.toString();
    }

}
