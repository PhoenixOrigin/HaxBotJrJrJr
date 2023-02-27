package net.Phoenix.features;

import net.Phoenix.utilities.Utilities;
import net.Phoenix.utilities.annotations.BridgeCommand;
import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.entities.Role;
import net.dv8tion.jda.api.events.interaction.command.CommandAutoCompleteInteractionEvent;
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;
import net.dv8tion.jda.api.hooks.SubscribeEvent;
import net.dv8tion.jda.api.interactions.commands.Command;
import net.dv8tion.jda.api.interactions.commands.OptionType;
import org.jetbrains.annotations.NotNull;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import static net.Phoenix.Main.database;

/*
OptionData data = new OptionData(OptionType.STRING, "name", "The name of the signup role", true, true);

        SlashCommandData command = Commands.slash("signup", "SignupRoles");
        SubcommandData ping = new SubcommandData("ping", "Ping a signup role");
        ping.addOptions(data);
        ping.addOption(OptionType.STRING, "message", "The message you would like to give");

        SubcommandData createPing = new SubcommandData("create", "Create a ping role");
        createPing.addOption(OptionType.STRING, "name", "The name of the signup role", true);

        SubcommandData leavePing = new SubcommandData("leave", "Leave a ping role");
        leavePing.addOption(OptionType.STRING, "name", "The name of the signup role", true, true);

        SubcommandData registerPing = new SubcommandData("port", "Port a ping role");
        registerPing.addOption(OptionType.STRING, "name", "The name of the signup role", true);
        registerPing.addOption(OptionType.ROLE, "role", "The existing role to turn into a signup role", true);

        SubcommandData signupPing = new SubcommandData("join", "Join a signup role");
        signupPing.addOptions(data);

        SubcommandData deletePing = new SubcommandData("delete", "Delete a ping role");
        deletePing.addOptions(data);

        command.addSubcommands(createPing, registerPing, signupPing, deletePing, ping);

 */

@BridgeCommand(name = "signup",
        description = "Multiple signup commands :)",
        subcommands = {
                @BridgeCommand.SubCommand(name = "ping",
                        description = "Ping a signup role",
                        options = {
                            @BridgeCommand.CommandOption(type = OptionType.STRING,
                                    name = "name",
                                    description = "The name of the signup role",
                                    required = true,
                                    autocomplete = true
                            )
                        }
                ),
                @BridgeCommand.SubCommand(name = "create",
                        description = "Create a signup role",
                        options = {
                                @BridgeCommand.CommandOption(type = OptionType.STRING,
                                        name = "name",
                                        description = "The name of the signup role",
                                        required = true,
                                        autocomplete = true
                                )
                        }
                ),
                @BridgeCommand.SubCommand(name = "leave",
                        description = "Leave a signup list",
                        options = {
                                @BridgeCommand.CommandOption(type = OptionType.STRING,
                                        name = "name",
                                        description = "The name of the signup role",
                                        required = true,
                                        autocomplete = true
                                )
                        }
                ),
                @BridgeCommand.SubCommand(name = "port",
                        description = "Port a signup command",
                        options = {
                                @BridgeCommand.CommandOption(type = OptionType.STRING,
                                        name = "name",
                                        description = "The name of the signup role",
                                        required = true,
                                        autocomplete = true
                                ),
                                @BridgeCommand.CommandOption(type = OptionType.ROLE,
                                        name = "role",
                                        description = "The current role to port"
                                )
                        }
                ),
                @BridgeCommand.SubCommand(name = "join",
                        description = "Join a signup list",
                        options = {
                                @BridgeCommand.CommandOption(type = OptionType.STRING,
                                        name = "name",
                                        description = "The name of the signup role",
                                        required = true,
                                        autocomplete = true
                                )
                        }
                ),
                @BridgeCommand.SubCommand(name = "delete",
                        description = "Delete a signup list",
                        options = {
                                @BridgeCommand.CommandOption(type = OptionType.STRING,
                                        name = "name",
                                        description = "The name of the signup role",
                                        required = true,
                                        autocomplete = true
                                )
                        }
                )
        }
)
public class SignupFeature {

    @SubscribeEvent
    public static void handleAutoComplete(@NotNull CommandAutoCompleteInteractionEvent event) {
        try {
            if (event.getName().equals("signup") && event.getFocusedOption().getName().equals("name")) {
                List<String> words = new ArrayList<>();
                PreparedStatement statement = database.prepareStatement("SELECT name FROM signup;");
                ResultSet set = statement.executeQuery();
                while (set.next()) {
                    words.add(set.getString(1));
                }
                List<Command.Choice> options = words.stream()
                        .map(word -> new Command.Choice(word, word))
                        .collect(Collectors.toList());
                event.replyChoices(options).queue();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @BridgeCommand.SubCommand(name = "leave")
    public static void leave(SlashCommandInteractionEvent event) {
        event.deferReply(true).queue();
        try {
            PreparedStatement statement = database.prepareStatement(String.format("UPDATE signup" +
                    " SET users = array_remove(users, %d)" +
                    " WHERE name='%s';", event.getMember().getUser().getIdLong(), event.getOption("name").getAsString()));
            statement.executeUpdate();
            event.getHook().editOriginal("Successfully removed you from " + event.getOption("name").getAsString()).queue();
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    @BridgeCommand.SubCommand(name = "create")
    public static void createRole(SlashCommandInteractionEvent event){
        event.deferReply(true).queue();

        if (Utilities.hasHigherRole(event.getMember(), event.getGuild().getRolesByName("Cadet", true).get(0))) {
            event.reply("You can't use this >:(").queue();
            return;
        }
        try (PreparedStatement statement = database.prepareStatement("INSERT INTO signup (name, users) VALUES (?, ?::BIGINT[])")) {
            statement.setString(1, event.getOption("name").getAsString());
            Array usersArray = database.createArrayOf("BIGINT", new ArrayList<Long>().toArray());
            statement.setArray(2, usersArray);
            statement.executeUpdate();
            event.getHook().editOriginal("Successfully created role").queue();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    @BridgeCommand.SubCommand(name = "port")
    public static void portRole(SlashCommandInteractionEvent event){
        event.deferReply(true).queue();

        if (Utilities.hasHigherRole(event.getMember(), event.getGuild().getRolesByName("Cosmonaut", true).get(0))) {
            event.reply("You can't use this >:(").queue();
            return;
        }
        Role toPort = event.getOption("role").getAsRole();
        List<Long> members = event.getGuild().getMembersWithRoles(toPort)
                .stream()
                .map(Member::getIdLong)
                .toList();
        toPort.delete().queue();
        try (PreparedStatement statement = database.prepareStatement("INSERT INTO signup (name, users) VALUES (?, ?::BIGINT[])")) {
            statement.setString(1, event.getOption("name").getAsString());
            Array usersArray = database.createArrayOf("BIGINT", members.toArray());
            statement.setArray(2, usersArray);
            statement.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
        event.getHook().editOriginal("Successfully ported and deleted the original role").queue();
    }

    @BridgeCommand.SubCommand(name = "join")
    public static void signup(SlashCommandInteractionEvent event) {
        event.deferReply(true).queue();

        try {
            PreparedStatement statement = database.prepareStatement(String.format("UPDATE signup" +
                    " SET users = array_append(users, %d)" +
                    " WHERE name='%s';", event.getMember().getUser().getIdLong(), event.getOption("name").getAsString()));
            statement.executeUpdate();
            event.getHook().editOriginal("Successfully signed you up to " + event.getOption("name").getAsString()).queue();
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }

    }

    @BridgeCommand.SubCommand(name = "delete")
    public static void delete(SlashCommandInteractionEvent event) {
        event.deferReply(true).queue();

        if (Utilities.hasHigherRole(event.getMember(), event.getGuild().getRolesByName("Cosmonaut", true).get(0))) {
            event.reply("You can't use this >:(").queue();
            return;
        }
        try {
            PreparedStatement statement = database.prepareStatement(String.format("DELETE FROM signup WHERE name='%s'", event.getOption("name").getAsString()));
            statement.executeUpdate();
            event.getHook().editOriginal("Successfully deleted the role " + event.getOption("name")).queue();
        } catch (SQLException e) {
            event.getHook().editOriginal("Un-Successfully deleted the role " + event.getOption("name")).queue();
        }
    }

    @BridgeCommand.SubCommand(name = "ping")
    public static void ping(SlashCommandInteractionEvent event){
        event.deferReply(true).queue();

        if (Utilities.hasHigherRole(event.getMember(), event.getGuild().getRolesByName("Milkyway resident", true).get(0))) {
            event.reply("You can't use this >:(").queue();
            return;
        }
        List<Long> userList = new ArrayList<>(); // create a new ArrayList to store the values
        try (Statement statement = database.createStatement();
             ResultSet resultSet = statement.executeQuery(String.format("SELECT users FROM signup WHERE name='%s'", event.getOption("name").getAsString()))) {
            resultSet.next();
            Array usersArray = resultSet.getArray(1);
            Long[] users = (Long[]) usersArray.getArray();
            userList.addAll(List.of(users));
        } catch (SQLException e) {
            e.printStackTrace();
        }
        StringBuilder mentions = new StringBuilder();
        for(long id : userList){
            mentions.append(" <@!").append(id).append(">");
        }
        try {
            event.getChannel().sendMessage(mentions.toString()).queue(msg -> {
                try {
                    String message = event.getOption("message").getAsString();
                    msg.editMessage(event.getMember().getAsMention() + " has mentioned " + event.getOption("name").getAsString() + " with the message: \n\n" + message).queue();
                } catch (NullPointerException | IllegalArgumentException ignored) {
                    msg.editMessage(event.getMember().getAsMention() + " has mentioned " + event.getOption("name").getAsString()).queue();
                }
            });
        } catch (IllegalStateException e ) {
            event.getChannel().sendMessage("No users have this signup :oof:").queue();
        }
    }

}
