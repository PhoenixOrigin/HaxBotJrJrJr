package net.Phoenix.trackers;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import net.Phoenix.Main;
import net.Phoenix.api.AthenaAPI;
import net.Phoenix.api.MojangAPI;
import net.Phoenix.api.objects.AthenaServerList;
import net.Phoenix.api.objects.MojangUUID;

import java.io.*;
import java.net.*;
import java.nio.charset.StandardCharsets;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.sql.SQLType;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.stream.Collectors;

public class Players {

    public static void updatePlayerPlaytime() throws IOException {
        AthenaServerList serverList = null;
        try {
            serverList = AthenaAPI.getAvailableServers();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        Connection database = Main.database;
        JsonArray array = new JsonArray();
        for(AthenaServerList.Server server : serverList.getServers()){

            List<String> serverPlayers = server.getPlayers();
            System.out.println(serverPlayers.size());
            for (String player : serverPlayers){
                array.add(player);
            }
        }
        URL url = null;
        try {
            url = new URL("https://www.example.com/login");
        } catch (MalformedURLException e) {
            throw new RuntimeException(e);
        }
        URLConnection con = null;
        try {
            con = url.openConnection();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        HttpURLConnection http = (HttpURLConnection)con;
        try {
            http.setRequestMethod("POST"); // PUT is another valid option
        } catch (ProtocolException e) {
            throw new RuntimeException(e);
        }
        http.setDoOutput(true);
        byte[] out = array.getAsString().getBytes(StandardCharsets.UTF_8);
        int length = out.length;

        http.setFixedLengthStreamingMode(length);
        http.setRequestProperty("Content-Type", "application/json; charset=UTF-8");
        try {
            http.connect();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        try(OutputStream os = http.getOutputStream()) {
            os.write(out);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        JsonArray obj = null;
        try(InputStream is = http.getInputStream()){
            String text = new BufferedReader(
                    new InputStreamReader(is, StandardCharsets.UTF_8))
                    .lines()
                    .collect(Collectors.joining("\n"));
            obj = JsonParser.parseString(text).getAsJsonArray();
        }

        for( JsonElement player : obj.asList()){
            try {
                PreparedStatement statement = database.prepareStatement("INSERT INTO playtime (uuid, playtime) VALUES (?, ?);");
                player.getValue()
                statement.setObject(1, player);
                statement.setInt(2, 5);
                statement.executeUpdate();
            } catch (SQLException exception) {
                exception.printStackTrace();
            }
        }
        System.out.println("workingeeeee");

    }

}
