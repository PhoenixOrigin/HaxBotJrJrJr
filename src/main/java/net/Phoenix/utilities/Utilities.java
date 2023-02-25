package net.Phoenix.utilities;

import com.google.common.base.Splitter;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import net.Phoenix.Main;
import net.Phoenix.api.MojangAPI;
import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.entities.Message;
import net.dv8tion.jda.api.entities.channel.concrete.TextChannel;
import net.dv8tion.jda.api.interactions.commands.OptionMapping;
import net.dv8tion.jda.api.interactions.commands.OptionType;

import java.awt.*;
import java.awt.font.TextAttribute;
import java.awt.geom.RoundRectangle2D;
import java.awt.image.BufferedImage;
import java.io.*;
import java.net.*;
import java.nio.charset.StandardCharsets;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.text.AttributedCharacterIterator;
import java.text.AttributedString;
import java.time.OffsetDateTime;
import java.time.Period;
import java.util.*;
import java.util.List;
import java.util.concurrent.*;
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
     *
     * Draws a centered string in the box created by the paramaters
     *
     * @param g Graphics object of image to draw on
     * @param topX Top x of box
     * @param topY Top y of box
     * @param bottomX Bottom x of box
     * @param bottomY Bottom y of box
     * @param text Text to draw
     */
    public static void drawCenteredMultilineString(Graphics g, int topX, int topY, int bottomX, int bottomY, String text) {
        FontMetrics metrics = g.getFontMetrics();
        int lineHeight = metrics.getHeight();

        int maxLineWidth = bottomX - topX;
        int y = topY + ((bottomY - topY - (lineHeight * getNumberOfLines(text))) / 2) + metrics.getAscent();

        String[] words = text.split(" ");
        StringBuilder sb = new StringBuilder();
        List<String> lines = new ArrayList<>();

        for (String word : words) {
            if (metrics.stringWidth(sb.toString() + " " + word) > maxLineWidth) {
                lines.add(sb.toString());
                sb.setLength(0);
            }
            sb.append(word).append(" ");
        }
        lines.add(sb.toString());

        for (int i = 0; i < lines.size(); i++) {
            String line = lines.get(i);
            int lineWidth = metrics.stringWidth(line);

            int x = topX + ((maxLineWidth - lineWidth) / 2);
            g.drawString(line, x, y);

            y += lineHeight;
        }
    }

    private static int getNumberOfLines(String text) {
        if (text == null || text.isEmpty()) {
            return 0;
        }
        return text.split("\r\n|\r|\n").length;
    }

    public static void drawTextWithOutline(Graphics2D g2d, String text, int x, int y, Color textColor, Color outlineColor, int outlineSize) {
        g2d.setColor(outlineColor);
        g2d.setStroke(new BasicStroke(outlineSize, BasicStroke.CAP_ROUND, BasicStroke.JOIN_ROUND));
        FontMetrics fontMetrics = g2d.getFontMetrics();
        int textWidth = fontMetrics.stringWidth(text);
        int textHeight = fontMetrics.getHeight();
        int textX = x;
        int textY = y + textHeight/2 - fontMetrics.getDescent();
        g2d.drawString(text, textX, textY);
        g2d.setColor(textColor);
        g2d.setStroke(new BasicStroke());
        g2d.drawString(text, textX, textY);
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

    /**
     * @param url The url to query
     * @return the string response
     * @throws IOException if the return value is null
     */
    public static String queryAPI(String url) throws IOException {
        return new BufferedReader(
                new InputStreamReader(new URL(url).openConnection().getInputStream(), StandardCharsets.UTF_8))
                .lines()
                .collect(Collectors.joining("\n"));
    }

    public static String queryAPI(String url, Map<String, String> headers) throws IOException {
        URL queryurl = new URL(url);
        HttpURLConnection http = (HttpURLConnection) queryurl.openConnection();
        for(Map.Entry<String, String> entry : headers.entrySet()){
            http.addRequestProperty(entry.getKey(), entry.getValue());
        }
        http.connect();
        return new BufferedReader(
                new InputStreamReader(http.getInputStream(), StandardCharsets.UTF_8))
                .lines()
                .collect(Collectors.joining("\n"));
    }

    public static String postAPI(String apiLink, String json) {
        HttpURLConnection http = null;
        try {
            URL url = new URL(apiLink);
            URLConnection con = null;
            con = url.openConnection();
            http = (HttpURLConnection) con;
            http.setRequestMethod("POST");
            http.setDoOutput(true);
            byte[] out = json.getBytes(StandardCharsets.UTF_8);
            int length = out.length;
            http.setFixedLengthStreamingMode(length);
            http.setRequestProperty("Content-Type", "application/json; charset=UTF-8");
            http.connect();
            OutputStream os = http.getOutputStream();
            os.write(out);
            InputStream is = http.getInputStream();
            return new BufferedReader(
                    new InputStreamReader(is, StandardCharsets.UTF_8))
                    .lines()
                    .collect(Collectors.joining("\n"));
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    public static UUID getPlayerUUID(Connection database, String player) {
        try {
            PreparedStatement statement = database.prepareStatement(String.format("Select uuid FROM uuidcache WHERE username = '%s'", player));
            ResultSet set = statement.executeQuery();
            if (set.next()) {
                return (UUID) set.getObject(1);
            } else {
                Map<String, String> headers = new HashMap<>();
                headers.put("user-agent", "amoghnrathi@gmail.com or phoenix#1691");
                JsonObject resp = JsonParser.parseString(queryAPI("https://playerdb.co/api/player/minecraft/" + player, headers)).getAsJsonObject();
                String value = resp.get("data").getAsJsonObject().get("player").getAsJsonObject().get("id").getAsString();
                if (value == null) throw new NullPointerException();
                PreparedStatement update = database.prepareStatement("INSERT INTO uuidcache (uuid, username) VALUES (?, ?)");
                UUID uuid = UUID.fromString(value);
                update.setObject(1, uuid);
                update.setString(2, player);
                update.executeUpdate();
                return uuid;
            }
        } catch (SQLException | IOException ignored) {
        }
        return null;
    }

    public static int getMessages(Member member, int gap, TimeUnit unit) {
        List<Message> messages = new ArrayList<>();
        final OffsetDateTime[] prevTime = {null};

        for (TextChannel channel : member.getGuild().getTextChannels()) {

            channel.getIterableHistory().forEachAsync(message -> {
                if (message.getAuthor().equals(member)) {
                    OffsetDateTime currTime = message.getTimeCreated();
                    if (prevTime[0] != null && currTime.minusMinutes(unit.toMinutes(gap)).isAfter(prevTime[0])) {
                        messages.add(message);
                    }
                    prevTime[0] = currTime;
                }
                return true;
            }).join();
        }

        return messages.size();
    }

    public static Object getOptionValue(OptionMapping option) {
        return switch (option.getType()) {
            case BOOLEAN -> option.getAsBoolean();
            case CHANNEL -> option.getAsChannel();
            case INTEGER -> option.getAsInt();
            case NUMBER -> option.getAsLong();
            case MENTIONABLE -> option.getAsMentionable();
            case ROLE -> option.getAsRole();
            case STRING -> option.getAsString();
            case UNKNOWN -> null;
            case ATTACHMENT -> option.getAsAttachment();
            case USER -> option.getAsUser();
            default -> throw new IllegalArgumentException("Unrecognized option type: " + option.getType());
        };
    }

    public static List<UUID> getPlayersUUIDs(List<String> player) {

        try {
            List<UUID> uuids = new ArrayList<>();
            Connection database = Main.database;
            List<Callable<UUID>> callableTasks = new ArrayList<>();
            for (String username : player) {
                Callable<UUID> callableTask = () -> getPlayerUUID(database, username);
                callableTasks.add(callableTask);
            }
            ExecutorService executorService = Executors.newFixedThreadPool(100);
            List<Future<UUID>> futures = executorService.invokeAll(callableTasks);
            for (Future<UUID> uuidFuture : futures) {
                UUID uuid = uuidFuture.get();
                if (uuid != null) {
                    uuids.add(uuidFuture.get());
                }
            }
            return uuids;
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

}
