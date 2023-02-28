package net.Phoenix;

import net.Phoenix.features.SignupFeature;
import net.Phoenix.handlers.ConfigHandler;
import net.Phoenix.handlers.TrackerHandler;
import net.Phoenix.utilities.RateLimit;
import net.Phoenix.utilities.annotationHandlers.EventAnnotationHandler;
import net.Phoenix.utilities.annotationHandlers.SlashCommandAnnotationHandler;
import net.Phoenix.utilities.paginators.embeds.MultiPagedEmbedHandler;
import net.Phoenix.utilities.paginators.messages.MultiPagedMessageHandler;
import net.dv8tion.jda.api.JDA;
import net.dv8tion.jda.api.JDABuilder;
import net.dv8tion.jda.api.entities.Activity;
import net.dv8tion.jda.api.requests.GatewayIntent;
import net.dv8tion.jda.api.utils.ChunkingFilter;
import net.dv8tion.jda.api.utils.MemberCachePolicy;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.concurrent.TimeUnit;

public class Main {

    public static JDA jda = null;
    public static Connection database = null;
    public static RateLimit playerRateLimit;
    public static RateLimit connectionRateLimit;
    public static MultiPagedEmbedHandler multiPagedEmbedHandler = new MultiPagedEmbedHandler();
    public static MultiPagedMessageHandler multiPagedMessageHandler = new MultiPagedMessageHandler();
    public static void main(String[] args) throws SQLException, ClassNotFoundException {
        // Initilise ConfigHandler
        ConfigHandler.init();
        // No point runing bot if all features disabled
        if (ConfigHandler.getConfigBool("disable_all")) return;

        if(ConfigHandler.getConfigBool("database")){
            Class.forName("org.postgresql.Driver");
            database = DriverManager.getConnection("jdbc:postgresql://localhost:5432/" + ConfigHandler.getConfigString("dbname"), ConfigHandler.getConfigString("dbusername"), ConfigHandler.getConfigString("dbpassword"));
            PreparedStatement statement = database.prepareStatement("CREATE TABLE IF NOT EXISTS playtime (uuid UUID PRIMARY KEY NOT NULL, playtime int NOT NULL, timestamp timestamp);");
            statement.execute();
            PreparedStatement statement2 = database.prepareStatement("CREATE TABLE IF NOT EXISTS uuidcache (uuid UUID PRIMARY KEY NOT NULL, username TEXT NOT NULL);");
            statement2.execute();
            PreparedStatement statement3 = database.prepareStatement("CREATE TABLE IF NOT EXISTS signup (name TEXT PRIMARY KEY NOT NULL, users BIGINT[] NOT NULL);");
            statement3.execute();

            TrackerHandler.queueTrackers();
        }

        // Creating a builder
        JDABuilder builder = JDABuilder.createDefault(ConfigHandler.getConfigString("token"))
                .setChunkingFilter(ChunkingFilter.ALL) // enable member chunking for all guilds
                .setMemberCachePolicy(MemberCachePolicy.ALL);
        // Making our guild member events fire
        builder.enableIntents(GatewayIntent.GUILD_MEMBERS);

        // Can be changed to literally anything
        builder.setActivity(Activity.watching("Watching over " + ConfigHandler.getConfigString("guild_name")));
        // Building
        jda = builder.build();
        ///jda.updateCommands().queue();
        jda.addEventListener(multiPagedEmbedHandler);
        jda.addEventListener(multiPagedMessageHandler);
        SlashCommandAnnotationHandler.registerCommands(jda);
        EventAnnotationHandler.registerEvents(jda);

        playerRateLimit = new RateLimit(180, 1, TimeUnit.MINUTES);
        connectionRateLimit = new RateLimit(50, 1, TimeUnit.SECONDS);

        Runtime.getRuntime().addShutdownHook(new Thread(() -> {
            connectionRateLimit.shutdown();
            playerRateLimit.shutdown();
        }));

    }

}