package net.Phoenix.trackers;

import net.Phoenix.Main;
import net.Phoenix.utilities.Utilities;
import net.Phoenix.api.AthenaAPI;
import net.Phoenix.api.objects.AthenaServerList;

import java.io.IOException;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.time.Instant;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import java.util.concurrent.ExecutionException;

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
        try {
            List<UUID> uuids = Utilities.getPlayersUUIDs(array);
            for(UUID uuid : uuids){
                try {
                    PreparedStatement statement = database.prepareStatement("INSERT INTO playtime (uuid, playtime, timestamp) VALUES (?, ?, ?);");
                    statement.setObject(1, uuid);

                    statement.setInt(2, 5);

                    statement.setTimestamp(3, Timestamp.from(Instant.now()));

                    statement.executeUpdate();
                } catch(SQLException e) {
                    throw new RuntimeException(e);
                }
            }
        } catch (SQLException | IOException | InterruptedException | ExecutionException e) {
            throw new RuntimeException(e);
        }
    }

}
