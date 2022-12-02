package net.Phoenix.features.commands;

import net.Phoenix.Main;
import net.Phoenix.Utilities;
import net.Phoenix.api.MojangAPI;
import net.Phoenix.api.objects.MojangUUID;
import net.Phoenix.handlers.ConfigHandler;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;

import java.awt.*;
import java.io.IOException;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.Duration;
import java.time.LocalTime;
import java.util.UUID;

public class PlaytimeCommand {

    public static void handleEvent(SlashCommandInteractionEvent event) {
        Connection database = Main.database;
        String username = event.getOption("name").getAsString();
        MojangUUID mojangUUID = null;
        try {
            mojangUUID = MojangAPI.getPlayerUUID(username);
        } catch(IOException | NullPointerException e){
            EmbedBuilder builder = new EmbedBuilder();
            builder.setTitle(username + "'s playtime");
            builder.setDescription("Monthly Playtime: 00h00m\n" +
                    "Weekly Playtime: 00h00m\n" +
                    "Weekly Playtime: 00h00m");
            builder.setColor(Color.BLACK);
            event.getHook().editOriginalEmbeds(builder.build()).queue();
            return;
        }
        UUID uuid = mojangUUID.getUuid();
        EmbedBuilder builder = new EmbedBuilder();
        builder.setTitle(username + "'s playtime");
        String message = "";
        try {
            PreparedStatement monthlyPlaytimeRequest = database.prepareStatement("SELECT SUM(playtime) FROM playtime WHERE uuid = ? AND timestamp > NOW() - INTERVAL '1 MONTH';");
            monthlyPlaytimeRequest.setObject(1, uuid);
            ResultSet monthlyPlaytime = monthlyPlaytimeRequest.executeQuery();
            if(!monthlyPlaytime.next()){
                event.getHook().editOriginal("The player has either not played Wynncraft since the implementation of playtime tracking or is invalid").queue();
                return;
            }
            message += "Monthly Playtime: " + LocalTime.MIN.plus(Duration.ofMinutes(monthlyPlaytime.getInt(1))).toString().replace(":", "h") + "m\n";

            PreparedStatement weeklyPlaytimeRequest = database.prepareStatement("SELECT SUM(playtime) FROM playtime WHERE uuid = ? AND timestamp > NOW() - INTERVAL '1 WEEK';");
            weeklyPlaytimeRequest.setObject(1, uuid);
            ResultSet weeklyPlaytime = weeklyPlaytimeRequest.executeQuery();
            if(!weeklyPlaytime.next()){
                event.getHook().editOriginal("The player has either not played Wynncraft since the implementation of playtime tracking or is invalid").queue();
                return;
            }
            message += "Weekly Playtime: " + LocalTime.MIN.plus(Duration.ofMinutes(weeklyPlaytime.getInt(1))).toString().replace(":", "h") + "m\n";

            PreparedStatement dailyPlaytimeRequest = database.prepareStatement("SELECT SUM(playtime) FROM playtime WHERE uuid = ? AND timestamp > NOW() - INTERVAL '1 DAY';");
            dailyPlaytimeRequest.setObject(1, uuid);
            ResultSet dailyPlaytime = dailyPlaytimeRequest.executeQuery();
            if(!dailyPlaytime.next()){
                event.getHook().editOriginal("The player has either not played Wynncraft since the implementation of playtime tracking or is invalid").queue();
                return;
            }
            message += "Weekly Playtime: " + LocalTime.MIN.plus(Duration.ofMinutes(dailyPlaytime.getInt(1))).toString().replace(":", "h") + "m\n";

        } catch(SQLException exception){
            exception.printStackTrace();
            Utilities.printError(exception, ConfigHandler.getConfigLong("error_channel"), event.getGuild());
        }

        builder.setDescription(message);
        builder.setColor(Color.BLACK);
        event.getHook().editOriginalEmbeds(builder.build()).queue();
    }

}
