package net.Phoenix;

import net.Phoenix.events.EventListener;
import net.Phoenix.handlers.ConfigHandler;
import net.Phoenix.handlers.TrackerHandler;
import net.dv8tion.jda.api.JDA;
import net.dv8tion.jda.api.JDABuilder;
import net.dv8tion.jda.api.entities.Activity;
import net.dv8tion.jda.api.interactions.commands.Command;
import net.dv8tion.jda.api.interactions.commands.OptionType;
import net.dv8tion.jda.api.interactions.commands.build.OptionData;
import net.dv8tion.jda.api.requests.GatewayIntent;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.SQLException;

public class Main {

    public static JDA jda = null;
    public static Connection database = null;

    public static void main(String[] args) throws SQLException, ClassNotFoundException {
        // Initilise ConfigHandler
        ConfigHandler.init();
        // No point runing bot if all features disabled
        if (ConfigHandler.getConfigBool("disable_all")) return;

        // Creating a builder
        JDABuilder builder = JDABuilder.createDefault(ConfigHandler.getConfigString("token"));

        // Making our guild member events fire
        builder.enableIntents(GatewayIntent.GUILD_MEMBERS);

        // Can be changed to literally anything
        builder.setActivity(Activity.watching("Watching over " + ConfigHandler.getConfigString("guild_name")));

        // Building
        jda = builder.build();

        // Registering the event handler
        jda.addEventListener(new EventListener());

        // Adding /sp command
        jda.upsertCommand("sp", "Lists all the available soul points")
                .addOption(OptionType.INTEGER, "offset", "Offset to aply to values", false)
                .addOption(OptionType.INTEGER, "count", "How many worlds do you want to see", false)
                .queue();

        OptionData feature = new OptionData(OptionType.STRING, "feature", "The name of the feature you would like to toggle", true);
        feature.addChoices(
                new Command.Choice("Disable all", "disable_all"),
                new Command.Choice("Soul point command", "soul_point_command"),
                new Command.Choice("Feature toggle command", "feature_enable_command"),
                new Command.Choice("Discord welcome image", "discord_welcome_image"),
                new Command.Choice("Database features", "database")
        );

        // Adding /feature command
        jda.upsertCommand("feature", "Toggle a certain feature")
                .addOptions(feature)
                .addOption(OptionType.BOOLEAN, "enabled", "Whether to enable or disable the feature", true)
                .queue();

        jda.upsertCommand("playtime", "Get the playtime of a certain player")
                .addOption(OptionType.STRING, "name", "The name of the player")
                .queue();

        if(ConfigHandler.getConfigBool("database")){
            Class.forName("org.postgresql.Driver");
            database = DriverManager.getConnection("jdbc:postgresql://localhost:5432/admin", "admin", "password");
            PreparedStatement statement = database.prepareStatement("CREATE TABLE IF NOT EXISTS playtime (uuid UUID PRIMARY KEY NOT NULL, playtime int NOT NULL);");
            statement.execute();
            TrackerHandler.queueTrackers();
        }
    }

}