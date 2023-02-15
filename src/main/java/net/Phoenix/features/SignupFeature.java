package net.Phoenix.features;

import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.entities.Role;
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;
import net.dv8tion.jda.api.interactions.commands.OptionType;
import net.dv8tion.jda.api.interactions.commands.SlashCommandInteraction;
import net.dv8tion.jda.api.interactions.commands.build.Commands;
import net.dv8tion.jda.api.interactions.commands.build.OptionData;
import net.dv8tion.jda.api.interactions.commands.build.SlashCommandData;
import net.dv8tion.jda.api.interactions.commands.build.SubcommandData;

import java.sql.*;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import static net.Phoenix.Main.database;

public class SignupFeature {

    public static void handleCommand(SlashCommandInteractionEvent event){
        switch (event.getSubcommandName()) {
            case "create" -> createRole(event.getInteraction());
            case "port" -> portRole(event.getInteraction());
            case "signup" -> signup(event.getInteraction());
            case "delete" -> delete(event.getInteraction());
            default -> ping(event.getInteraction());
        }
    }

    public static void createRole(SlashCommandInteraction interaction){

    }

    public static void portRole(SlashCommandInteraction interaction){
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
        interaction.reply("Successfully ported and deleted the original role").setEphemeral(true).queue();

    }

    public static void signup(SlashCommandInteraction interaction) {
        try {
            PreparedStatement statement = database.prepareStatement(String.format("UPDATE signup" +
                    "SET users = array_append(users, %d)" +
                    "WHERE name = '%s';", interaction.getMember().getUser().getIdLong(), interaction.getOption("name").getAsString()));
            statement.executeUpdate();
            interaction.reply("Successfully signed you up to " + interaction.getOption("name")).queue();
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }

    }

    public static void delete(SlashCommandInteraction interaction) {
        try {
            PreparedStatement statement = database.prepareStatement(String.format("DELETE FROM signup WHERE name = '%s'", interaction.getOption("name").getAsString()));
            statement.executeUpdate();
            interaction.reply("Successfully deleted the role " + interaction.getOption("name")).queue();
        } catch (SQLException e) {
            interaction.reply("Un-Successfully deleted the role " + interaction.getOption("name")).queue();
        }
    }

    public static void ping(SlashCommandInteraction interaction){
        List<Long> userList = new ArrayList<>(); // create a new ArrayList to store the values

        try (Statement statement = database.createStatement();
             ResultSet resultSet = statement.executeQuery(String.format("SELECT users FROM signup WHERE name = '%s'", interaction.getOption("name").getAsString()))) {
            resultSet.next();
            Array usersArray = resultSet.getArray("users");
            Long[] users = (Long[]) usersArray.getArray();
            userList.addAll(Arrays.asList(users));
        } catch (SQLException e) {
            e.printStackTrace();
        }
        List<String> mentions = new ArrayList<>();
        for(long id : userList){
            interaction.getGuild().retrieveMemberById(id).queue(member -> {
                mentions.add(member.getAsMention());
            });
        }
        interaction.getChannel().sendMessage(String.join(" ", mentions)).queue(msg -> {
            try{
                String message = interaction.getOption("message").getAsString();
                msg.editMessage(interaction.getMember().getAsMention() + " has mentioned you with the message: \n\n" + message).queue();
            } catch (IllegalArgumentException ignored){
                interaction.getChannel().asTextChannel().sendMessage(interaction.getMember().getAsMention() + " has mentioned you");
            }
        });
    }

    public static SlashCommandData createCommand() throws SQLException {
        OptionData data = new OptionData(OptionType.STRING, "name", "The name of the signup role", true);
        PreparedStatement statement = database.prepareStatement("SELECT name FROM signup;");
        ResultSet set = statement.executeQuery();
        set.next();
        while(true){
            data.addChoice(set.getString("name"), set.getString(" name"));
            if(!set.next()) break;
        }

        SlashCommandData command = Commands.slash("ping", "Ping a role");
        command.addOptions(data);
        command.addOption(OptionType.STRING, "message", "The message you would like to give");

        SubcommandData createPing = new SubcommandData("create", "Create a ping role");
        createPing.addOption(OptionType.STRING, "name", "The name of the signup role", true);

        SubcommandData registerPing = new SubcommandData("port", "Pot a ping role");
        createPing.addOption(OptionType.STRING, "name", "The name of the signup role", true);
        createPing.addOption(OptionType.ROLE, "role", "The existing role to turn into a signup role", true);

        SubcommandData signupPing = new SubcommandData("signup", "Signup to a ping role");
        signupPing.addOptions(data);

        SubcommandData deletePing = new SubcommandData("delete", "Delete a ping role");
        deletePing.addOptions(data);

        command.addSubcommands(createPing, registerPing, signupPing, deletePing);

        return command;
    }

}
