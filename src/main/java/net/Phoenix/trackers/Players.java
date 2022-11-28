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
import net.Phoenix.api.objects.MojangUUIDList;

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
import java.util.concurrent.ExecutionException;
import java.util.stream.Collectors;

public class Players {

    public static void updatePlayerPlaytime() {
        AthenaServerList serverList;
        try {
            serverList = AthenaAPI.getAvailableServers();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }

        Connection database = Main.database;
        List<String> array = new ArrayList<>();
        for(AthenaServerList.Server server : serverList.getServers()){
            List<String> serverPlayers = server.getPlayers();
            array.addAll(serverPlayers);
        }

        MojangUUIDList uuids;
        try {
            uuids = MojangAPI.getPlayerUUIDs(array);
        } catch (InterruptedException | ExecutionException e) {
            throw new RuntimeException(e);
        }

        for(MojangUUIDList.MojangPlayer player : uuids.getPlayers()){
            try {
                PreparedStatement statement = database.prepareStatement("INSERT INTO playtime (uuid, playtime) VALUES (?, ?);");
                statement.setObject(1, player.getUuid());
                statement.setInt(2, 5);
                statement.executeUpdate();
            } catch(SQLException e) {
                throw new RuntimeException(e);
            }
        }
    }

}
