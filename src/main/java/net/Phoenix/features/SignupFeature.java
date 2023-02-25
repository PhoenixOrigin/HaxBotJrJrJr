package net.Phoenix.features;

import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.entities.Role;
import net.dv8tion.jda.api.events.interaction.command.CommandAutoCompleteInteractionEvent;
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;
import net.dv8tion.jda.api.interactions.commands.Command;
import net.dv8tion.jda.api.interactions.commands.OptionType;
import net.dv8tion.jda.api.interactions.commands.SlashCommandInteraction;
import net.dv8tion.jda.api.interactions.commands.build.*;
import org.jetbrains.annotations.NotNull;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import static net.Phoenix.Main.database;

public class SignupFeature {

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

    public static void handleCommand(SlashCommandInteractionEvent event){
        switch (event.getSubcommandName()) {
            case "create" -> createRole(event.getInteraction());
            case "port" -> portRole(event.getInteraction());
            case "join" -> signup(event.getInteraction());
            case "delete" -> delete(event.getInteraction());
            case "leave" -> leave(event.getInteraction());
            default -> ping(event.getInteraction());
        }
    }


    public static void leave(SlashCommandInteraction interaction) {
        try {
            PreparedStatement statement = database.prepareStatement(String.format("UPDATE signup" +
                    " SET users = array_remove(users, %d)" +
                    " WHERE name='%s';", interaction.getMember().getUser().getIdLong(), interaction.getOption("name").getAsString()));
            statement.executeUpdate();
            interaction.getHook().editOriginal("Successfully removed you from " + interaction.getOption("name").getAsString()).queue();
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    public static void createRole(SlashCommandInteraction interaction){
        if (interaction.getMember().canInteract(interaction.getGuild().getRolesByName("Cadet", true).get(0))) {
            interaction.reply("You can't use this >:(").queue();
            return;
        }
        try (PreparedStatement statement = database.prepareStatement("INSERT INTO signup (name, users) VALUES (?, ?::BIGINT[])")) {
            statement.setString(1, interaction.getOption("name").getAsString());
            Array usersArray = database.createArrayOf("BIGINT", new ArrayList<Long>().toArray());
            statement.setArray(2, usersArray);
            statement.executeUpdate();
            interaction.getHook().editOriginal("Successfully created role").queue();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public static void portRole(SlashCommandInteraction interaction){
        if (interaction.getMember().canInteract(interaction.getGuild().getRolesByName("Cosmonaut", true).get(0))) {
            interaction.reply("You can't use this >:(").queue();
            return;
        }
        Role toPort = interaction.getOption("role").getAsRole();
        List<Long> members = interaction.getGuild().getMembersWithRoles(toPort)
                .stream()
                .map(Member::getIdLong)
                .toList();
        toPort.delete().queue();
        try (PreparedStatement statement = database.prepareStatement("INSERT INTO signup (name, users) VALUES (?, ?::BIGINT[])")) {
            statement.setString(1, interaction.getOption("name").getAsString());
            Array usersArray = database.createArrayOf("BIGINT", members.toArray());
            statement.setArray(2, usersArray);
            statement.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
        interaction.getHook().editOriginal("Successfully ported and deleted the original role").queue();
    }

    public static void signup(SlashCommandInteraction interaction) {
        try {
            PreparedStatement statement = database.prepareStatement(String.format("UPDATE signup" +
                    " SET users = array_append(users, %d)" +
                    " WHERE name='%s';", interaction.getMember().getUser().getIdLong(), interaction.getOption("name").getAsString()));
            statement.executeUpdate();
            interaction.getHook().editOriginal("Successfully signed you up to " + interaction.getOption("name").getAsString()).queue();
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }

    }

    public static void delete(SlashCommandInteraction interaction) {
        if (interaction.getMember().canInteract(interaction.getGuild().getRolesByName("Cosmonaut", true).get(0))) {
            interaction.reply("You can't use this >:(").queue();
            return;
        }
        try {
            PreparedStatement statement = database.prepareStatement(String.format("DELETE FROM signup WHERE name='%s'", interaction.getOption("name").getAsString()));
            statement.executeUpdate();
            interaction.getHook().editOriginal("Successfully deleted the role " + interaction.getOption("name")).queue();
        } catch (SQLException e) {
            interaction.getHook().editOriginal("Un-Successfully deleted the role " + interaction.getOption("name")).queue();
        }
    }

    public static void ping(SlashCommandInteraction interaction){
        if (interaction.getMember().canInteract(interaction.getGuild().getRolesByName("Milkyway resident", true).get(0))) {
            interaction.reply("You can't use this >:(").queue();
            return;
        }
        List<Long> userList = new ArrayList<>(); // create a new ArrayList to store the values
        try (Statement statement = database.createStatement();
             ResultSet resultSet = statement.executeQuery(String.format("SELECT users FROM signup WHERE name='%s'", interaction.getOption("name").getAsString()))) {
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
            interaction.getChannel().sendMessage(mentions.toString()).queue(msg -> {
                try {
                    String message = interaction.getOption("message").getAsString();
                    msg.editMessage(interaction.getMember().getAsMention() + " has mentioned " + interaction.getOption("name").getAsString() + " with the message: \n\n" + message).queue();
                } catch (NullPointerException | IllegalArgumentException ignored) {
                    msg.editMessage(interaction.getMember().getAsMention() + " has mentioned " + interaction.getOption("name").getAsString()).queue();
                }
            });
        } catch (IllegalStateException e ) {
            interaction.getChannel().sendMessage("No users have this signup :oof:").queue();
        }
    }

    public static SlashCommandData createCommand() {
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

        return command;
    }

}
