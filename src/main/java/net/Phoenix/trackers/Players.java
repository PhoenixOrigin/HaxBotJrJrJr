package net.Phoenix.trackers;

import net.Phoenix.Main;
import net.Phoenix.api.AthenaAPI;
import net.Phoenix.api.MojangAPI;
import net.Phoenix.api.objects.AthenaServerList;
import net.Phoenix.api.objects.MojangUUID;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.sql.SQLType;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public class Players {

    public static void updatePlayerPlaytime(){
        AthenaServerList serverList = AthenaAPI.getAvailableServers();
        List<UUID> players = new ArrayList<>();
        Connection database = Main.database;
        for(AthenaServerList.Server server : serverList.getServers()){
            List<String> serverPlayers = server.getPlayers();
            for (String player : serverPlayers){
                MojangUUID uuid = MojangAPI.getPlayerUUID(player);
                players.add(uuid.getUuid());
            }
        }
        for(UUID player : players){
            try {
                PreparedStatement statement = database.prepareStatement("INSERT INTO playtime (uuid, playtime) VALUES (?, ?);");
                statement.setObject(1, player);
                statement.setInt(2, 5);
                statement.executeUpdate();
            } catch (SQLException exception) {
                exception.printStackTrace();
            }
        }
    }

}
