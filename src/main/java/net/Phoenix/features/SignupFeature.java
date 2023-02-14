package net.Phoenix.features;

import net.Phoenix.Main;
import net.Phoenix.handlers.SlashCommandHandler;
import net.dv8tion.jda.api.entities.Role;
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;
import net.dv8tion.jda.api.interactions.commands.OptionType;
import net.dv8tion.jda.api.interactions.commands.SlashCommandInteraction;
import net.dv8tion.jda.api.interactions.commands.build.*;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

import static net.Phoenix.Main.database;

public class SignupFeature {

    public static void handleCommand(SlashCommandInteractionEvent event){
        switch (event.getSubcommandName()) {
            case "create" -> createRole(event.getInteraction());
            case "register" -> registerRole(event.getInteraction());
            case "signup" -> signup(event.getInteraction());
            case "delete" -> delete(event.getInteraction());
            default -> ping(event.getInteraction());
        }
    }

    public static void createRole(SlashCommandInteraction interaction){

    }

    public static void registerRole(SlashCommandInteraction interaction){

    }

    public static void signup(SlashCommandInteraction interaction) {

    }

    public static void delete(SlashCommandInteraction interaction) {

    }

    public static void ping(SlashCommandInteraction interaction){
        try {
            PreparedStatement statement = database.prepareStatement(String.format("SELECT roleid FROM signup WHERE name = %s", interaction.getOption("name")));
            ResultSet response = statement.executeQuery();
            response.next();
            long roleid = response.getLong("roleid");
            Role role = interaction.getGuild().getRoleById(roleid);

            try{
                String message = interaction.getOption("message").getAsString();
                interaction.getChannel().asTextChannel().sendMessage(interaction.getMember().getAsMention() + " has mentioned " + role.getAsMention() + " with the message: \n\n" + message);

            } catch (IllegalArgumentException ignored){
                interaction.getChannel().asTextChannel().sendMessage(interaction.getMember().getAsMention() + " has mentioned " + role.getAsMention());
            }
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
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
        createPing.addOption(OptionType.STRING, "hex", "The hex code of the signup role (no #)");
        createPing.addOption(OptionType.STRING, "name", "The name of the signup role", true);

        SubcommandData registerPing = new SubcommandData("register", "Register a ping role");
        createPing.addOption(OptionType.STRING, "name", "The name of the signup role", true);
        createPing.addOption(OptionType.ROLE, "role", "The existing role to turn into a ping role", true);

        SubcommandData signupPing = new SubcommandData("signup", "Signup to a ping role");
        signupPing.addOptions(data);

        SubcommandData deletePing = new SubcommandData("delete", "Delete a ping role");
        deletePing.addOptions(data);

        command.addSubcommands(createPing, registerPing, signupPing, deletePing);

        return command;
    }

}
