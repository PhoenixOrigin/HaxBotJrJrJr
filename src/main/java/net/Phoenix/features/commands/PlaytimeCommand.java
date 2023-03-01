package net.Phoenix.features.commands;

import net.Phoenix.Main;
import net.Phoenix.api.WynncraftAPI;
import net.Phoenix.api.objects.Player;
import net.Phoenix.utilities.Utilities;
import net.Phoenix.api.MojangAPI;
import net.Phoenix.api.objects.MojangUUID;
import net.Phoenix.handlers.ConfigHandler;
import net.Phoenix.utilities.annotations.BridgeCommand;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;
import net.dv8tion.jda.api.interactions.commands.OptionType;

import java.awt.*;
import java.io.IOException;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.Duration;
import java.time.LocalTime;
import java.util.UUID;

@BridgeCommand(name = "playtime",
        description = "Get a user's playtime",
        options = {
                @BridgeCommand.CommandOption(type = OptionType.STRING, name = "name", description = "The IGN of the player who's playtime you wanna check", required = true)
        }
)
public class PlaytimeCommand {

    @BridgeCommand.invoke
    public static void handleEvent(SlashCommandInteractionEvent event) {
        event.deferReply(true).queue();

        Connection database = Main.database;
        String username = event.getOption("name").getAsString();
        UUID uuid = Utilities.getPlayerUUID(username);
        EmbedBuilder builder = new EmbedBuilder();
        builder.setTitle(username.replace("_", "\\_").replace("*", "\\*") + "'s playtime");
        String message = "";
        try {
            PreparedStatement monthlyPlaytimeRequest = database.prepareStatement("SELECT SUM(playtime) " +
                    "FROM playtime " +
                    "WHERE uuid = ? " +
                    " AND DATE_TRUNC('month', timestamp) = DATE_TRUNC('month', CURRENT_DATE);");
            monthlyPlaytimeRequest.setObject(1, uuid);
            ResultSet monthlyPlaytime = monthlyPlaytimeRequest.executeQuery();
            if(!monthlyPlaytime.next()){
                event.getHook().editOriginal("The player has either not played Wynncraft since the implementation of playtime tracking or is invalid").queue();
                return;
            }
            message += "Monthly Playtime: " + LocalTime.MIN.plus(Duration.ofMinutes(monthlyPlaytime.getInt(1))).toString().replace(":", "h") + "m\n";

            PreparedStatement weeklyPlaytimeRequest =  database.prepareStatement("SELECT SUM(playtime) " +
                    "FROM playtime " +
                    "WHERE uuid = ? " +
                    " AND DATE_TRUNC('week', timestamp) = DATE_TRUNC('week', CURRENT_DATE);");
            weeklyPlaytimeRequest.setObject(1, uuid);
            ResultSet weeklyPlaytime = weeklyPlaytimeRequest.executeQuery();
            if(!weeklyPlaytime.next()){
                event.getHook().editOriginal("The player has either not played Wynncraft since the implementation of playtime tracking or is invalid").queue();
                return;
            }
            message += "Weekly Playtime: " + LocalTime.MIN.plus(Duration.ofMinutes(weeklyPlaytime.getInt(1))).toString().replace(":", "h") + "m\n";

            PreparedStatement dailyPlaytimeRequest =  database.prepareStatement("SELECT SUM(playtime) " +
                    "FROM playtime " +
                    "WHERE uuid = ? " +
                    " AND DATE_TRUNC('day', timestamp) = DATE_TRUNC('day', CURRENT_DATE);");
            dailyPlaytimeRequest.setObject(1, uuid);
            ResultSet dailyPlaytime = dailyPlaytimeRequest.executeQuery();
            if(!dailyPlaytime.next()){
                event.getHook().editOriginal("The player has either not played Wynncraft since the implementation of playtime tracking or is invalid").queue();
                return;
            }
            message += "Today's Playtime: " + LocalTime.MIN.plus(Duration.ofMinutes(dailyPlaytime.getInt(1))).toString().replace(":", "h") + "m\n";

        } catch(SQLException exception){
            exception.printStackTrace();
            Utilities.printError(exception, ConfigHandler.getConfigLong("error_channel"), event.getGuild());
        }

        builder.setDescription(message);
        builder.setColor(Color.BLACK);
        event.getHook().editOriginalEmbeds(builder.build()).queue();
    }

}
