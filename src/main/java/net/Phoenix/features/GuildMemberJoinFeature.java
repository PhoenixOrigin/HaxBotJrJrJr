package net.Phoenix.features;

import net.Phoenix.handlers.ConfigHandler;
import net.Phoenix.Main;
import net.Phoenix.Utilities;
import net.dv8tion.jda.api.events.guild.member.GuildMemberJoinEvent;
import net.dv8tion.jda.api.utils.FileUpload;

import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.awt.image.RenderedImage;
import java.io.File;
import java.io.IOException;
import java.util.concurrent.ExecutionException;

public class GuildMemberJoinFeature {

    public static void handleEvent(GuildMemberJoinEvent event) {
        long errorChannel = ConfigHandler.getConfigLong("error_channel");
        // Getting Icon
        Image icon = null;
        try {
            icon = ImageIO.read(event.getMember().getEffectiveAvatar().download().get());
        } catch (InterruptedException | ExecutionException | IOException exception) {
            Utilities.printError(exception, errorChannel, event.getGuild());
        }
        icon = icon.getScaledInstance(600, 600, Image.SCALE_DEFAULT);
        icon = Utilities.makeRoundedCorner(Utilities.toBufferedImage(icon), 600);

        // Getting the background image
        Image background = null;
        try {
            background = ImageIO.read(Main.class.getResource("/images/backgroundImage.png"));
        } catch (IOException exception) {
            Utilities.printError(exception, errorChannel, event.getGuild());
        }

        // Getting graphics handler
        Graphics graphics = background.getGraphics();

        // Draw Their Icon
        graphics.drawImage(icon, 100, 147, null);

        // Readable font
        graphics.setFont(graphics.getFont().deriveFont(50f));

        //Draw their config text
        Utilities.drawCenteredString(graphics, ConfigHandler.getConfigString("welcome_message").replace("{username}", event.getMember().getEffectiveName()), 800, 800, 0, 894);

        // Creating a temp file with image
        File tempFile = null;
        try {
            tempFile = File.createTempFile("image", ".png");
            ImageIO.write((RenderedImage) background, "PNG", tempFile);
        } catch (IOException exception) {
            Utilities.printError(exception, errorChannel, event.getGuild());
        }

        event
                .getGuild()
                .getTextChannelById(ConfigHandler.getConfigLong("welcome_channel"))
                .sendFiles(FileUpload.fromData(tempFile))
                .queue();

    }

}
