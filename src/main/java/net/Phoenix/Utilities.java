package net.Phoenix;

import com.google.common.base.Splitter;
import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.entities.channel.concrete.TextChannel;

import java.awt.*;
import java.awt.geom.RoundRectangle2D;
import java.awt.image.BufferedImage;
import java.io.*;
import java.net.MalformedURLException;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

public class Utilities {

    /**
     * Prints error to a specific JDA discord server
     *
     * @param exception The exception you need to print
     * @param channelId The id of the channel you want to print this to
     * @param guild     The server which the channel is in
     */
    public static void printError(Throwable exception, long channelId, Guild guild) {
        // Getting error channel
        TextChannel errorChannel = guild.getTextChannelById(channelId);
        // Making empty stringwriter
        StringWriter sw = new StringWriter();
        // Making printwriter that writes to stringwriter
        PrintWriter pw = new PrintWriter(sw);
        // Printing error to stringwriter
        exception.printStackTrace(pw);
        // Turning it to a string
        String stack = sw.toString();
        // Splitting it into every 2000characters if its massive
        List<String> stackTrace = Splitter.fixedLength(2000).splitToList(stack);
        // Sending stackTrace
        for (String string : stackTrace) {
            // Sent!
            errorChannel.sendMessage(string).queue();
        }
    }

    /**
     * Draws text in a box centered.
     *
     * @param g         The graphics of the image you want to overlay the text on
     * @param text      The text to be overlayed
     * @param lineWidth Max line text width
     * @param topX      Box top x
     * @param topY      Box top y
     * @param bottomY   Height of box
     */
    public static void drawCenteredString(Graphics g, String text, int lineWidth, int topX, int topY, int bottomY) {
        // Getting Font Metrics
        FontMetrics metrics = g.getFontMetrics();

        // Making a list with separate lines
        List<String> lines = new ArrayList<>();

        // If text is short enough adding to list else working through it
        if (metrics.stringWidth(text) < lineWidth) {
            lines.add(text);
        } else {
            // Splitting the words
            List<String> words = List.of(text.split(" "));

            // Making a stringbuilder
            StringBuilder line = new StringBuilder();

            // Looping through words
            for (String word : words) {
                // Adding line to list if meeets size
                if (metrics.stringWidth(line.toString()) + metrics.stringWidth(word) >= lineWidth) {
                    lines.add(line.toString());
                    line = new StringBuilder();
                } else {
                    // Adding word if not
                    line.append(word);
                    line.append(" ");
                }
            }
        }

        // Getting how tall our text would be
        int textHeight = lines.size() * metrics.getHeight();

        // Getting startY
        int y = ((bottomY - topY) - textHeight) / 2;

        // Drawing text
        for (String line : lines) {
            g.drawString(line, topX, y);
            y += metrics.getHeight();
        }
    }

    /**
     * Converts a given Image into a BufferedImage
     *
     * @param img The Image to be converted
     * @return The converted BufferedImage
     */
    public static BufferedImage toBufferedImage(Image img) {
        if (img instanceof BufferedImage) {
            return (BufferedImage) img;
        }

        // Create a buffered image with transparency
        BufferedImage bimage = new BufferedImage(img.getWidth(null), img.getHeight(null), BufferedImage.TYPE_INT_ARGB);

        // Draw the image on to the buffered image
        Graphics2D bGr = bimage.createGraphics();
        bGr.drawImage(img, 0, 0, null);
        bGr.dispose();

        // Return the buffered image
        return bimage;
    }

    public static BufferedImage makeRoundedCorner(BufferedImage image, int cornerRadius) {
        int w = image.getWidth();
        int h = image.getHeight();
        BufferedImage output = new BufferedImage(w, h, BufferedImage.TYPE_INT_ARGB);

        Graphics2D g2 = output.createGraphics();

        // This is what we want, but it only does hard-clipping, i.e. aliasing
        // g2.setClip(new RoundRectangle2D ...)

        // so instead fake soft-clipping by first drawing the desired clip shape
        // in fully opaque white with antialiasing enabled...
        g2.setComposite(AlphaComposite.Src);
        g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
        g2.setColor(Color.WHITE);
        g2.fill(new RoundRectangle2D.Float(0, 0, w, h, cornerRadius, cornerRadius));

        // ... then compositing the image on top,
        // using the white shape from above as alpha source
        g2.setComposite(AlphaComposite.SrcAtop);
        g2.drawImage(image, 0, 0, null);

        g2.dispose();

        return output;
    }

    public static String queryAPI(String url) throws IOException {
        return new BufferedReader(
                    new InputStreamReader(new URL(url).openConnection().getInputStream(), StandardCharsets.UTF_8))
                    .lines()
                    .collect(Collectors.joining("\n"));
    }
}
