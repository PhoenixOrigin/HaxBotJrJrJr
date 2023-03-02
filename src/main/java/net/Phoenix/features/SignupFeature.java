package net.Phoenix.features;

import com.iwebpp.crypto.TweetNaclFast;
import net.Phoenix.utilities.Utilities;
import net.Phoenix.utilities.annotations.BridgeCommand;
import net.Phoenix.utilities.annotations.Event;
import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.entities.Role;
import net.dv8tion.jda.api.events.interaction.command.CommandAutoCompleteInteractionEvent;
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;
import net.dv8tion.jda.api.interactions.commands.Command;
import net.dv8tion.jda.api.interactions.commands.OptionType;
import org.apache.commons.collections4.functors.InstantiateFactory;
import org.jetbrains.annotations.NotNull;

import java.sql.*;
import java.text.SimpleDateFormat;
import java.time.Duration;
import java.time.Instant;
import java.time.LocalTime;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.stream.Collectors;

import static net.Phoenix.Main.database;

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
                            ),
                                @BridgeCommand.CommandOption(type = OptionType.STRING,
                                        name = "message",
                                        description = "The message to send :)"
                                )
                        }
                ),
                @BridgeCommand.SubCommand(name = "create",
                        description = "Create a signup role",
                        options = {
                                @BridgeCommand.CommandOption(type = OptionType.STRING,
                                        name = "name",
                                        description = "The name of the signup role",
                                        required = true
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
                                        required = true
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

    private static HashMap<String, Instant> timer = new HashMap<>();

    @Event(eventType = CommandAutoCompleteInteractionEvent.class)
    public void onCommandAutoCompleteInteraction(@NotNull CommandAutoCompleteInteractionEvent event) {
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

        if (!Utilities.hasHigherRole(event.getMember(), event.getGuild().getRolesByName("Cadet", true).get(0))) {
            event.getHook().editOriginal("You can't use this >:(").queue();
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

        if (!Utilities.hasHigherRole(event.getMember(), event.getGuild().getRolesByName("Cosmonaut", true).get(0))) {
            event.getHook().editOriginal("You can't use this >:(").queue();
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

        if (!Utilities.hasHigherRole(event.getMember(), event.getGuild().getRolesByName("Cosmonaut", true).get(0))) {
            event.getHook().editOriginal("You can't use this >:(").queue();
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
        String name = event.getOption("name").getAsString();
        try {
            if (ChronoUnit.MINUTES.between(timer.get(name), Instant.now()) >= 3) {
                timer.put(name, Instant.now());
            } else {
                Duration duration = Duration.ofMinutes(3).minus(Duration.between(timer.get(name), Instant.now()));
                long minutes = duration.toMinutes();
                long seconds = duration.minusMinutes(minutes).getSeconds();

                event.getHook()
                        .editOriginal(String.format(
                                "This signup role is on cooldown! Please wait %dm %ds!",
                                minutes,
                                seconds
                        ))
                        .queue();
                return;
            }
        } catch (NullPointerException e) {
            timer.put(name, Instant.now());
        }

        if (!Utilities.hasHigherRole(event.getMember(), event.getGuild().getRolesByName("Milkyway resident", true).get(0))) {
            event.getHook().editOriginal("You can't use this >:(").queue();
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
