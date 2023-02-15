package net.Phoenix.trackers;

import net.Phoenix.Main;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.time.Instant;
import java.util.List;
import java.util.UUID;

public class Players {

    public static void updatePlayerPlaytime(List<UUID> uuids) {
        Connection database = Main.database;
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
    }

    public static void updatePlayerRaids(List<UUID> uuids){

    }

}
