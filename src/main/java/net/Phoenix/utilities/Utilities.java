package net.Phoenix.utilities;

import com.google.common.base.Splitter;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import net.Phoenix.Main;
import net.Phoenix.api.MojangAPI;
import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.entities.channel.concrete.TextChannel;

import java.awt.*;
import java.awt.geom.RoundRectangle2D;
import java.awt.image.BufferedImage;
import java.io.*;
import java.net.*;
import java.nio.charset.StandardCharsets;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
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
