package net.Phoenix.features;

import com.google.common.base.Splitter;
import net.Phoenix.Main;
import net.Phoenix.utilities.Utilities;
import net.Phoenix.handlers.ConfigHandler;
import net.dv8tion.jda.api.events.guild.member.GuildMemberJoinEvent;
import net.dv8tion.jda.api.hooks.SubscribeEvent;
import net.dv8tion.jda.api.utils.FileUpload;

import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.image.RenderedImage;
import java.io.File;
import java.io.IOException;
import java.util.concurrent.ExecutionException;

public class GuildMemberJoinFeature {

    @SubscribeEvent
    public static void handleEvent(GuildMemberJoinEvent event) {
        long errorChannel = ConfigHandler.getConfigLong("error_channel");
        Image icon = null;
        try {
            icon = ImageIO.read(event.getMember().getEffectiveAvatar().download(150).get());
        } catch (InterruptedException | ExecutionException | IOException exception) {
            Utilities.printError(exception, errorChannel, event.getGuild());
        }
        icon = Utilities.makeRoundedCorner(Utilities.toBufferedImage(icon), 250);
        Image background = null;
        try {
            background = ImageIO.read(Main.class.getResource("/images/backgroundImage.png"));
        } catch (IOException exception) {
            Utilities.printError(exception, errorChannel, event.getGuild());
        }
        Graphics graphics = background.getGraphics();
        try {
            graphics.setFont(Font.createFont(Font.TRUETYPE_FONT, Main.class.getResourceAsStream("/fonts/audiowide.ttf")));
        } catch (FontFormatException | IOException e) {
            throw new RuntimeException(e);
        }
        graphics.drawImage(icon, 975 , 175, null);
        graphics.setFont(graphics.getFont().deriveFont(50f));
        if(event.getMember().getEffectiveName().length() >= 10) {
            FontMetrics fontMetrics = graphics.getFontMetrics();
            int textWidth = fontMetrics.stringWidth(Splitter.fixedLength(10).splitToList(event.getMember().getEffectiveName()).get(0) + "...");
            int x1 = 920;
            int x2 = 1275;
            int y = 550;
            int midpoint = (x1 + x2) / 2;
            int textX = midpoint - (textWidth / 2);
            graphics.drawString(Splitter.fixedLength(10).splitToList(event.getMember().getEffectiveName()).get(0) + "...", textX, y);
            textWidth = fontMetrics.stringWidth("#" + event.getMember().getUser().getDiscriminator());
            textX = midpoint - (textWidth / 2);
            graphics.drawString("#" + event.getMember().getUser().getDiscriminator(), textX, 550 + graphics.getFontMetrics().getHeight());
        } else {
            FontMetrics fontMetrics = graphics.getFontMetrics();
            int textWidth = fontMetrics.stringWidth(event.getMember().getEffectiveName());
            int x1 = 920;
            int x2 = 1275;
            int y = 550;
            int midpoint = (x1 + x2) / 2;
            int textX = midpoint - (textWidth / 2);
            graphics.drawString(event.getMember().getEffectiveName(), textX, y);
            textWidth = fontMetrics.stringWidth("#" + event.getMember().getUser().getDiscriminator());
            textX = midpoint - (textWidth / 2);
            graphics.drawString("#" + event.getMember().getUser().getDiscriminator(), textX, 550 + graphics.getFontMetrics().getHeight());
        }
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
